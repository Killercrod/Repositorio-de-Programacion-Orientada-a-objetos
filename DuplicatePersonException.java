package mx.ine.ocr.db.exception;

/**
 * Excepción lanzada cuando se detecta un intento de insertar una persona
 * que ya existe (misma clave única) en la base de datos.
 */
public class DuplicatePersonException extends RuntimeException {
    public DuplicatePersonException(String mensaje) {
        super(mensaje);
    }
}
