package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.OCRData;
import com.example.demo.service.OCRDataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CapturaController {

    @Autowired
    private OCRDataService ocrDataService;

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

            String trimmed = body.trim();
            
            // Si la respuesta ya es JSON, intentar guardar en BD
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                try {
                    // Parsear JSON y guardar en BD
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> jsonResponse = mapper.readValue(body, Map.class);
                    
                    // Extraer datos de la respuesta
                    Object resultadoObj = jsonResponse.get("resultado");
                    if (resultadoObj instanceof Map) {
                        Map<String, Object> resultado = (Map<String, Object>) resultadoObj;
                        
                        // Crear entidad OCRData con los datos
                        OCRData ocrData = new OCRData();
                        
                        // Campos críticos (priorizando curp_detectado)
                        String curpDetectado = (String) resultado.get("curp_detectado");
                        if (curpDetectado != null && !curpDetectado.trim().isEmpty()) {
                            ocrData.setCurpDetectado(curpDetectado.trim().toUpperCase());
                            ocrData.setTipoDocumento("INE");
                            ocrData.setDocumentoIdentificado(true);
                            
                            // Campos opcionales
                            if (resultado.containsKey("nombre")) {
                                ocrData.setNombre((String) resultado.get("nombre"));
                            }
                            if (resultado.containsKey("apellido")) {
                                ocrData.setApellido((String) resultado.get("apellido"));
                            }
                            if (resultado.containsKey("apellido_materno")) {
                                ocrData.setApellidoMaterno((String) resultado.get("apellido_materno"));
                            }
                            if (resultado.containsKey("fecha_nacimiento")) {
                                ocrData.setFechaNacimiento((String) resultado.get("fecha_nacimiento"));
                            }
                            if (resultado.containsKey("confianza")) {
                                Object confianzaObj = resultado.get("confianza");
                                if (confianzaObj instanceof Double) {
                                    ocrData.setConfianzaValor((Double) confianzaObj);
                                } else if (confianzaObj instanceof String) {
                                    ocrData.setConfianza((String) confianzaObj);
                                }
                            }
                            if (resultado.containsKey("direccion")) {
                                ocrData.setDireccion((String) resultado.get("direccion"));
                            }
                            
                            // Intentar guardar en BD
                            try {
                                OCRData guardado = ocrDataService.guardarOCRData(ocrData);
                                String respuesta = "{\"estado\":\"EXITO\",\"mensaje\":\"Datos OCR capturados y guardados\",\"id\":" + guardado.getId() + ",\"curp\":\"" + escapeJson(guardado.getCurpDetectado()) + "\",\"resultado\":" + body + "}";
                                return ResponseEntity.ok(respuesta);
                            } catch (IllegalArgumentException e) {
                                // Duplicado de CURP
                                return ResponseEntity.status(409).body(
                                    "{\"estado\":\"DUPLICADO\",\"error\":\"" + escapeJson(e.getMessage()) + "\",\"resultado\":" + body + "}"
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    // Si hay error al parsear o guardar, devolver la respuesta original
                    return ResponseEntity.ok(body);
                }
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
