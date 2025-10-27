package mx.ine.ocr.db.dao;

import mx.ine.ocr.db.exception.DuplicatePersonException;
import mx.ine.ocr.db.model.Person;
import mx.ine.ocr.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * DAO (Data Access Object) responsable de las operaciones CRUD relacionadas
 * con la entidad Person. Contiene la lógica para generar una clave única a
 * partir de los datos de la persona y para insertar la persona rechazando
 * duplicados cuando la clave ya existe.
 */
public class PersonDAO {

    /**
     * Genera una clave única (SHA-256 en hexadecimal) para una persona.
     *
     * El propósito de esta clave es detectar entradas duplicadas cuando
     * el OCR extrae exactamente los mismos datos. Se normalizan los campos
     * (lowercase + trim) antes de hashear.
     *
     * Parám: p - persona con los campos que servirán para la clave
     * Retorna: string hex del hash SHA-256
     */
    public static String generarClave(Person p) {
        try {
            // Normalizamos los campos relevantes para minimizar diferencias
            // por mayúsculas/minúsculas o espacios accidentales.
            // Incluir CURP si está disponible para que la clave dependa también de ella.
            String normalizado = (safe(p.getNombre()) + "|" + safe(p.getApellido()) + "|" + safe(p.getFechaNacimiento()) + "|" + safe(p.getCurp())).toLowerCase(Locale.ROOT).trim();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalizado.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre debería existir en JVM modernas; si no, lanzamos runtime
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper: devuelve cadena segura (no-nula) para evitar NPEs durante
     * la concatenación en la generación de la clave.
     */
    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * Inserta una persona en la BD.
     * - Calcula la clave con `generarClave` y la asigna a la entidad.
     * - Antes de insertar, ejecuta una consulta para verificar si ya existe
     *   una persona con la misma clave. Si existe, lanza
     *   DuplicatePersonException para indicar que la inserción debe ser
     *   rechazada por duplicado.
     * - Si no existe, abre una transacción, guarda la entidad y hace commit.
     *
     * Importante: HQL usa los nombres de propiedades Java (clavePersona) en
     * la clase Person para consultar.
     */
    public void agregarPersona(Person p) {
        String clave = generarClave(p);
        p.setClavePersona(clave);

        // Abrimos una sesión; usamos try-with-resources para cerrarla automáticamente
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Buscar por clave existente (prevención adicional aparte del constraint DB)
            Query<Person> q = session.createQuery("from Person where clavePersona = :k", Person.class);
            q.setParameter("k", clave);
            Person existente = q.uniqueResult();
            if (existente != null) {
                // Si ya hay una persona con la misma clave, rechazamos
                throw new DuplicatePersonException("La persona ya existe en la base de datos (clave=" + clave + ")");
            }

            // Si no existe, guardamos dentro de una transacción
            Transaction tx = session.beginTransaction();
            session.save(p);
            tx.commit();
        }
    }
}
