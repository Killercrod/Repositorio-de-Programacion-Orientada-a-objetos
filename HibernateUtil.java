package mx.ine.ocr.db.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Utilidad para inicializar y exponer el SessionFactory de Hibernate.
 *
 * Razón: centralizar la creación del SessionFactory y evitar que múltiples
 * partes del código creen instancias separadas. Se carga la configuración
 * desde `hibernate.cfg.xml`.
 */
public class HibernateUtil {
    // SessionFactory es costoso de crear; lo hacemos una vez y lo reutilizamos.
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration();
            // Carga hibernate.cfg.xml desde resources
            cfg.configure("hibernate.cfg.xml");
            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            // Mensaje en español para facilitar diagnóstico
            System.err.println("Error al crear SessionFactory inicial: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /** Devuelve el SessionFactory singleton */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /** Cierra el SessionFactory (llamar al terminar la aplicación) */
    public static void shutdown() {
        getSessionFactory().close();
    }
}
