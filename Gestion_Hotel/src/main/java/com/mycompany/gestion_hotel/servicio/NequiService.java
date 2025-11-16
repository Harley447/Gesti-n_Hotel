/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestion_hotel.servicio;
// NequiService.java - Servicio para generar QR y consultar pagos en Nequi Conecta
// Nota: Reemplaza los valores de credenciales y termina la implementación de AWS Signature V4

import java.net.http.*;
import java.net.URI;
import java.util.Base64;
import java.util.UUID;

public class NequiService {

    private final String apiKey = "TU_API_KEY";
    private final String apiSecret = "TU_API_SECRET";
    private final String clientId = "TU_CLIENT_ID";

    private final String endpointQR = "https://api.nequi.com/payments/v2/-services-paymentservice-generatecodeqr";
    private final String endpointStatus = "https://api.nequi.com/payments/v2/-services-paymentservice-getstatuspayment";

    public String generarQR(double total) {
        try {
            String reference = "orden-" + UUID.randomUUID();
            long amount = (long)(total * 100);

            String body = "{"
                + "\"amountInCents\":" + amount + ","
                + "\"reference\":\"" + reference + "\"" 
                + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointQR))
                .header("Content-Type", "application/json")
                .header("Authorization", generarFirma(body))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                String json = resp.body();
                int idx = json.indexOf("qrCode");
                if (idx > 0) {
                    int start = json.indexOf('"', idx + 8) + 1;
                    int end = json.indexOf('"', start);
                    return json.substring(start, end);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String consultarEstado(String qrCode) {
        try {
            String body = "{ \"qrCode\": \"" + qrCode + "\" }";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointStatus))
                .header("Content-Type", "application/json")
                .header("Authorization", generarFirma(body))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                return resp.body();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generarURLImagenQR(String qrCode) {
        return "https://docs.conecta.nequi.com.co/qr-image?code=" + qrCode;
    }

    private String generarFirma(String body) {
        return "Firma-AWS-V4-Temporal-" + Base64.getEncoder().encodeToString(body.getBytes());
    }
}

// ---------------------------------------------------------------
// Ejemplo de uso en VentasController.java
// ---------------------------------------------------------------
// Dentro de tu controlador, declara:
// private NequiService nequi = new NequiService();
// Luego, en finalizarVenta():
// if (metodo.equalsIgnoreCase("Transferencia")) {
//      String qr = nequi.generarQR(total);
//      if (qr != null) {
//          mostrarQR(nequi.generarURLImagenQR(qr));
//          String estado = nequi.consultarEstado(qr);
//          System.out.println("Estado del pago: " + estado);
//      }
// }
