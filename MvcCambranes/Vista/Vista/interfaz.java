// import Modelo.Ciudadanos;
package Vista;
import Modelo.Ciudadanos;

import java.util.ArrayList;

import Controlador.ValidadorUsuario;

public class interfaz {
    public static void main(String[] args) {
        // Usuarios de prueba
        ArrayList<Ciudadanos> usuarios = new ArrayList<>();
        usuarios.add(new Ciudadanos("Juan", "Perez", "Lopez", "PEPJ8A0101HDFRRN09", 5551234567L, "juan@mail.com", "Av. Uno 10"));
        usuarios.add(new Ciudadanos("Ana", "Lopez", "Martinez", "LOAA900202MDFRRN08", 5552345678L, "ana@mail.com", "Calle Dos 20"));
        usuarios.add(new Ciudadanos("", "Gomez", "Ruiz", "GOLU800101HDFRRN07", 555345678L, "luis@mail.com", "Calle Tres 30")); // Nombre vacío
        usuarios.add(new Ciudadanos("Maria", "Ruiz", "Santos", "RUMA950404MDFRRN06", 55545L, "maria@mail.com", "Calle Cuatro 40")); // Teléfono corto
        usuarios.add(new Ciudadanos("Carlos", "Diaz", "Fernandez", "DICA920505HDFRRN05", 5555678901L, "carlos@mail.com", "Calle Cinco 50"));
        usuarios.add(new Ciudadanos("Cesar", "Lopez", "Alvarez", "LOAA990202MDFRRN08", 5556789012L, "cesar@mail.com", "Calle Seis 60"));
        usuarios.add(new Ciudadanos("Roberto", "Sanchez", "Mendez", "SARR850715HDFRRN12", 5512345678L, "roberto.sanchez@mail.com", "Calle Siete 70"));

        // Código postal de prueba (puedes agregarlo como atributo si lo necesitas)
        String[] codigosPostales = {"06000", "06700", "11560", "03100", "03A20", "06100", "06600"};

        for (int i = 0; i < usuarios.size(); i++) {
            Ciudadanos u = usuarios.get(i);
            System.out.println("\n--- Usuario de prueba " + (i+1) + " ---");
            System.out.println(u);

            boolean nombreValido = ValidadorUsuario.validarNombre(u.getNombre());
            boolean curpValido = ValidadorUsuario.validarCurp(u.getCurp());
            boolean telefonoValido = ValidadorUsuario.validarTelefono(Long.toString(u.getTel()));
            boolean cpValido = ValidadorUsuario.validarCodigoPostal(codigosPostales[i]);
            int edad = ValidadorUsuario.edadDesdeCurp(u.getCurp());
            boolean edadValida = u.validarEdad(edad);

            System.out.println("Validación de nombre: " + (nombreValido ? "Válido" : "Inválido"));
            System.out.println("Validación de CURP: " + (curpValido ? "Válido" : "Inválido"));
            System.out.println("Validación de teléfono: " + (telefonoValido ? "Válido" : "Inválido"));
            System.out.println("Validación de código postal: " + (cpValido ? "Válido" : "Inválido"));
            System.out.println("Edad calculada desde CURP: " + edad);
            System.out.println("Validación de edad (>18): " + (edadValida ? "Válido" : "Inválido"));
        }
    }
}