package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CapturaController {
    @GetMapping("/pycode")
    public String capturarFoto() {
        String url = "http://localhost:5000/tomar-foto";
        RestTemplate restTemplate = new RestTemplate();
        try{
            String respuesta = restTemplate.getForObject( url, String.class);
            return respuesta;
        } catch (Exception e){
            return "Error al conectar con el servidor Python: "+ e.getMessage();
        }
    }
    
}
