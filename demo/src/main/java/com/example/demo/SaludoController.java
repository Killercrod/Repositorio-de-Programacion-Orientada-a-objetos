package com.example.demo; 

import org.springframework.web.bind.annotation.*;

@RestController
public class SaludoController {

    @PostMapping("/saludo")
    //Llamada Request a los parametros necesarios (los datos del usuario)
    public String saludar(@RequestParam String nombre,
                          @RequestParam String apellidoPaterno,
                          @RequestParam(required = false) String apellidoMaterno) {
        //Mensaje mostrado en la consola 
        System.out.println("Nombre recibido: " + nombre); // se ve en la terminal
        System.out.println("Apellidos recibidos: " + apellidoPaterno + apellidoMaterno);

        //Mensaje enviado a la pagina 
        return "¡Hola " + nombre +" " + apellidoPaterno +" " + apellidoMaterno +" !Bienvenido :)";
    }
}

