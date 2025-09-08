package Controlador;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ValidadorUsuario {

    public static boolean validarCurp(String curp) {
        if (curp.length() != 18) return false;
        // Fecha en posiciones 4-9 (AAMMDD)
        for (int i = 4; i <= 9; i++) {
            if (!Character.isDigit(curp.charAt(i))) return false;
        }
        int anio = Integer.parseInt(curp.substring(4, 6));
        int anioReal = (anio < 30) ? (2000 + anio) : (1900 + anio);
        int anioActual = LocalDate.now().getYear();
        if (anioReal > anioActual || anioReal < 1900) return false;
        int mes = Integer.parseInt(curp.substring(6, 8));
        if (mes < 1 || mes > 12) return false;
        int dia = Integer.parseInt(curp.substring(8, 10));
        if (dia < 1 || dia > 31) return false;
        return true;
    }

    public static boolean validarTelefono(String telefono) {
        if (telefono.length() != 10) return false;
        for (int i = 0; i < 10; i++) {
            if (!Character.isDigit(telefono.charAt(i))) return false;
        }
        return true;
    }

    public static boolean validarCodigoPostal(String cp) {
        if (cp.length() != 5) return false;
        for (int i = 0; i < 5; i++) {
            if (!Character.isDigit(cp.charAt(i))) return false;
        }
        return true;
    }

    public static boolean validarNombre(String nombre) {
        return nombre != null && !nombre.isEmpty();
    }

    // Para la función curp_duplicada, necesitas una estructura de datos (por ejemplo, ArrayList<Usuario>)
    // Aquí solo se muestra la firma:
    // public static boolean curpDuplicada(String curp, List<Usuario> usuarios) { ... }

    public static int edadDesdeCurp(String curp) {
        int anio = Integer.parseInt(curp.substring(4, 6));
        int mes = Integer.parseInt(curp.substring(6, 8));
        int dia = Integer.parseInt(curp.substring(8, 10));
        int anioReal = (anio < 30) ? (2000 + anio) : (1900 + anio);

        LocalDate fechaNacimiento = LocalDate.of(anioReal, mes, dia);
        LocalDate hoy = LocalDate.now();

        int edad = hoy.getYear() - fechaNacimiento.getYear();
        if (hoy.getMonthValue() < fechaNacimiento.getMonthValue() ||
            (hoy.getMonthValue() == fechaNacimiento.getMonthValue() && hoy.getDayOfMonth() < fechaNacimiento.getDayOfMonth())) {
            edad--;
        }
        return edad;
    }
}

