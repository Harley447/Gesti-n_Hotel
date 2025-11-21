package com.mycompany.gestion_hotel.servicio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.http.*;
import java.net.URI;
import java.net.URL;

public class WompiService {

    private static final String API_URL = "https://sandbox.wompi.co/v1";
    private static final String PUBLIC_KEY = "pub_test_OLtBT4qyZjCNyKQWxMeU78bcJP83ogbh";
    private static final String PRIVATE_KEY = "prv_test_Ax53OukUQI10cyzajRnJseuuftMthHZT";
    private static final String INTEGRITY_KEY = "test_integrity_vUx7k949kYYjI4lVxz3KQa2zbgqSwSVM";

    private final HttpClient client = HttpClient.newHttpClient();
    private String acceptanceToken = null;

    public WompiService() {
        obtenerAcceptanceToken();
    }

    // ====================================
    // 1. OBTENER ACCEPTANCE TOKEN
    // ====================================
    private void obtenerAcceptanceToken() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/merchants/" + PUBLIC_KEY))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            acceptanceToken = extraerCampo(body, "\"acceptance_token\":\"", "\"");

            if (acceptanceToken == null) {
                System.err.println("❌ No se pudo extraer el acceptance_token del JSON: " + body);
            } else {
                System.out.println("Acceptance Token CORRECTO: " + acceptanceToken);
            }

        } catch (Exception e) {
            System.err.println("Error obteniendo acceptance_token: " + e.getMessage());
        }
    }

    // ====================================
    // 2. CREAR TRANSACCIÓN NEQUI
    // ====================================
   // ====================================
// 2. CREAR TRANSACCIÓN BANCOLOMBIA_QR
// ====================================
public String crearTransaccionBancolombiaQR(double monto) {
    try {
        if (acceptanceToken == null) {
            System.err.println("❌ No se puede crear transacción sin acceptance_token.");
            return null;
        }

        URL url = new URL(API_URL + "/transactions");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String referencia = "venta_" + System.currentTimeMillis();
        int amountInCents = (int)(monto * 100);
        String signature = generarSignature(referencia, amountInCents, "COP");

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Authorization", "Bearer " + PRIVATE_KEY);
        con.setDoOutput(true);

        String jsonBody =
            "{"
                + "\"amount_in_cents\":" + amountInCents + ","
                + "\"currency\":\"COP\","
                + "\"customer_email\":\"arleyeduardomantillavelasquez@gmail.com\","
                + "\"acceptance_token\":\"" + acceptanceToken + "\","
                + "\"reference\":\"" + referencia + "\","
                + "\"signature\":\"" + signature + "\","
               + "\"payment_method\":{"
+     "\"type\":\"BANCOLOMBIA_QR\","
+     "\"sandbox_status\":\"APPROVED\""
+ "}"

            + "}";

        System.out.println("JSON enviado: " + jsonBody);

        con.getOutputStream().write(jsonBody.getBytes("UTF-8"));

        int statusCode = con.getResponseCode();

        BufferedReader br;
        if (statusCode >= 200 && statusCode < 300) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        con.disconnect();

        System.out.println("Respuesta BANCOLOMBIA_QR: " + response);

        if (statusCode == 422) {
            System.err.println("⚠️ ERROR 422: JSON inválido o firma incorrecta.");
            return null;
        }

        if (statusCode == 401) {
            System.err.println("⚠️ ERROR 401: Llave privada inválida.");
            return null;
        }

        return extraerCampo(response.toString(), "\"id\":\"", "\"");

    } catch (Exception e) {
        System.err.println("Error creando transacción Bancolombia QR: " + e.getMessage());
        return null;
    }
}


    // ====================================
    // 3. CONSULTAR ESTADO
    // ====================================
    public String consultarEstado(String transactionId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/transactions/" + transactionId))
                    .header("Authorization", "Bearer " + PUBLIC_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return extraerCampo(response.body(), "\"status\":\"", "\"");

        } catch (Exception e) {
            System.err.println("Error consultando estado: " + e.getMessage());
        }
        return "ERROR";
    }

    // ====================================
    // 4. OBTENER QR DESDE TRANSACCIÓN
    // ====================================
    public String obtenerQRDesdeTransaccion(String transactionId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/transactions/" + transactionId))
                    .header("Authorization", "Bearer " + PUBLIC_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            System.out.println("JSON Transacción: " + json);

            return extraerCampo(json, "\"qr_code\":\"", "\"");

        } catch (Exception e) {
            System.err.println("Error obteniendo QR: " + e.getMessage());
            return null;
        }
    }

    // ====================================
    // 5. EXTRAER VALOR JSON
    // ====================================
    private String extraerCampo(String json, String inicio, String fin) {
        int i = json.indexOf(inicio);
        if (i == -1) return null;
        int j = json.indexOf(fin, i + inicio.length());
        if (j == -1) return null;
        return json.substring(i + inicio.length(), j);
    }

    // ====================================
    // 6. GENERAR FIRMA CON INTEGRITY_KEY
    // ====================================
    private String generarSignature(String referencia, int amountInCents, String currency) {
        try {
            String data = referencia + amountInCents + currency + INTEGRITY_KEY;

            System.out.println("String base para firma: " + data);

            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append("0");
                hexString.append(hex);
            }
            String signature = hexString.toString();
            System.out.println("Signature generado: " + signature);
            return signature;

        } catch (Exception e) {
            System.err.println("Error generando signature: " + e.getMessage());
            return null;
        }
    }
}
