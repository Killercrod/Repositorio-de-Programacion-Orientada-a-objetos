package com.example.demo; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dao.PersonDAO;
import com.example.demo.exception.DuplicatePersonException;
import com.example.demo.model.Person;

@RestController
public class BDconection {

    @Autowired
    private PersonDAO personDAO;

    @PostMapping("/saludo")
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
        System.out.println("-------------------------------------------");

        try {
            // Crear objeto Person con los datos del formulario
            Person persona = new Person();
            persona.setNombre(nombre);
            persona.setApellido(apellidoPaterno);
            persona.setApellidoMaterno(apellidoMaterno);
            persona.setCurp(curp);
            // Agregar más campos si es necesario según tu entidad Person
            
            // Guardar en BD usando PersonDAO
            personDAO.agregarPersona(persona);
            
            return "CORRECTO";
        } catch (DuplicatePersonException e) {
            return "ERROR: Persona duplicada - " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}