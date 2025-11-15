package com.example.demo;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CapturaController {

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    @GetMapping(value = "/pycode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> capturarFoto() {
        String url = "http://localhost:5000/tomar-foto";
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            String body = resp.getBody();
            if (body == null) body = "";

            // Si la respuesta ya es JSON (comienza con { o [), reenvía tal cual.
            String trimmed = body.trim();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                return ResponseEntity.status(resp.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(body);
            }

            // Si no es JSON, envolver en un objeto JSON
            String wrapped = "{\"mensaje\":\"OK\",\"resultado\":\"" + escapeJson(body) + "\"}";
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(wrapped);

        } catch (Exception e) {
            String err = "{\"error\":\"Error al conectar con el servidor Python: " + escapeJson(e.getMessage()) + "\"}";
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(err);
        }
    }
}
