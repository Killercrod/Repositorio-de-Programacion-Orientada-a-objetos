package com.example.demo.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OCRController {

    @PostMapping("/procesar-ine")
    public ResponseEntity<?> procesarINE(@RequestParam("foto") MultipartFile foto) {
        try {
            // Crear RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            
            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Preparar body con la foto
            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("foto", new MultipartInputStreamFileResource(foto.getInputStream(), foto.getOriginalFilename()));
            
            // Crear request entity
            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = 
                new HttpEntity<>(body, headers);
            
            // Llamar al servicio Python OCR local
            String pythonOCRUrl = "http://localhost:5001/procesar-ine";
            ResponseEntity<Map> response = restTemplate.postForEntity(
                pythonOCRUrl, requestEntity, Map.class);
            
            // Devolver la respuesta del servicio Python
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("estado", "ERROR");
            errorResponse.put("error", "Error al procesar INE: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Clase auxiliar para manejar el archivo multipart
    private static class MultipartInputStreamFileResource extends org.springframework.core.io.InputStreamResource {
        private final String filename;
        
        public MultipartInputStreamFileResource(java.io.InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }
        
        @Override
        public String getFilename() {
            return this.filename;
        }
        
        @Override
        public long contentLength() {
            return -1; // No sabemos el length
        }
    }
}