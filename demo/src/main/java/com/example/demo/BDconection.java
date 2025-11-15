package com.example.demo; 

import org.springframework.web.bind.annotation.*;

@RestController
public class BDconection {

    @PostMapping("/saludo")
    //Llamada Request a los parametros necesarios (los datos del usuario)
    public String saludar(@RequestParam String nombre,
                          @RequestParam String apellidoPaterno,
                          @RequestParam(required = false) String apellidoMaterno,
                          @RequestParam String curp,
                          @RequestParam String direccion,
                          @RequestParam String cruzamientos,
                          @RequestParam String codigopostal,
                          @RequestParam String correo,
                          @RequestParam String telefono){

        //Mensaje mostrado en la terminal para verificar los datos que se recibieron 
        System.out.println("-------------DATOS DE USUARIO-------------");
        System.out.println("Nombre recibido: " + nombre);
        System.out.println("Apellidos recibidos: " + apellidoPaterno + " " + apellidoMaterno);
        System.out.println("CURP del ciudadano: " + curp);
        System.out.println("DIRECCION: " + "\n" 
                            + "Direccion principal: " + direccion + "\n"
                            + "Cruzamientos: " + cruzamientos + "\n"
                            + "Codigo Postal: " + codigopostal);
        System.out.println("Correo electronico: " +correo);
        System.out.println("Numero de telefono: " + telefono);
        //System.out.println("Tel. Alterno: " + telefonoalterno);
        System.out.println("-------------------------------------------");
        //Mensaje de retorno a la página para corroborar que se recibieron correctamente los datos
        return "CORRECTO";
    }
}

