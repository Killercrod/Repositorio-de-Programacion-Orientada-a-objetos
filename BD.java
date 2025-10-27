package mx.ine.ocr.db;

import mx.ine.ocr.db.dao.PersonDAO;
import mx.ine.ocr.db.exception.DuplicatePersonException;
import mx.ine.ocr.db.model.Person;
import mx.ine.ocr.db.util.HibernateUtil;

/**
 * Clase demo para mostrar el flujo básico de la base de datos en memoria.
 *
 * Flujo:
 * 1) Crear DAO
 * 2) Construir dos objetos Person con los mismos datos
 * 3) Intentar insertar la primera (debe añadirse)
 * 4) Intentar insertar la segunda (debe rechazarse por duplicado)
 */
public class BD {
	public static void main(String[] args) {
		PersonDAO dao = new PersonDAO();

		// Datos de ejemplo que vendrían del OCR
		Person persona1 = new Person("Juan", "Perez", "1980-05-01");
		Person persona2 = new Person("Juan", "Perez", "1980-05-01"); // mismos datos -> debe rechazarse

		// Intento de insertar primera persona
		try {
			dao.agregarPersona(persona1);
			System.out.println("Persona agregada: " + persona1);
		} catch (Exception e) {
			// Manejo simple: imprimir el error en la consola
			System.err.println("Fallo al agregar persona1: " + e.getMessage());
		}

		// Intento de insertar persona duplicada
		try {
			dao.agregarPersona(persona2);
			System.out.println("Persona agregada: " + persona2);
		} catch (DuplicatePersonException dpe) {
			// Excepción esperada en caso de duplicado
			System.out.println("Duplicado detectado, se rechaza: " + dpe.getMessage());
		} catch (Exception e) {
			System.err.println("Fallo al agregar persona2: " + e.getMessage());
		}

		// Al finalizar cerramos el SessionFactory para liberar recursos
		HibernateUtil.shutdown();
	}
}
