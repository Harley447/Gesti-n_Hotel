package com.mycompany.gestion_hotel.servicio;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class WompiService {

    private static final String API_URL = "https://sandbox.wompi.co/v1";
    private static final String PUBLIC_KEY = "pub_test_CZPXZMcWuYmCfFAjY5qNmxx3H7gO6dEW"; // TU PUBLIC KEY
    private static final String PAYMENT_LINK_ID = "176094"; // TU PAYMENT LINK

    private final HttpClient client = HttpClient.newHttpClient();

    // Crear transacción
    public String crearTransaccion(double monto) {
        try {
            String body = String.format(
                "public_key=%s&payment_link_id=%s&amount_in_cents=%d&currency=COP",
                PUBLIC_KEY,
                PAYMENT_LINK_ID,
                (int)(monto * 100)
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/payment_links/" + PAYMENT_LINK_ID + "/transactions"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                return extraerCampo(response.body(), "\"id\":\"", "\"");
            }

            System.out.println("Error creando transacción: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Obtener URL del QR
    public String obtenerQR(String transactionId) {
        return "https://checkout.wompi.co/payment/qr/" + transactionId;
    }

    // Consultar estado del pago
    public String consultarEstado(String transactionId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/transactions/" + transactionId))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return extraerCampo(response.body(), "\"status\":\"", "\"");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    private String extraerCampo(String json, String inicio, String fin) {
        int i = json.indexOf(inicio);
        if (i == -1) return null;
        int j = json.indexOf(fin, i + inicio.length());
        if (j == -1) return null;
        return json.substring(i + inicio.length(), j);
    }
}
