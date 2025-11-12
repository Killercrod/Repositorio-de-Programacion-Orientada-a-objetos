package mx.ine.ocr.db.model;

import javax.persistence.*;

@Entity
@Table(name = "persona", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"clave_persona"})
})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave_persona", nullable = false, unique = true, length = 128)
    private String clavePersona; // clave única generada (hash)

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento; // formato simplificado YYYY-MM-DD

    public Person() {
    }

    public Person(String nombre, String apellido, String fechaNacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
    }

    package mx.ine.ocr.db.model;

    import javax.persistence.*;

    /**
     * Entidad JPA que representa una persona en la base de datos.
     *
     * Notas:
     * - La tabla en la BD se llama "persona".
     * - La columna "clave_persona" almacena el hash único generado a partir
     *   de los datos relevantes (nombre|apellido|fechaNacimiento). Esta
     *   columna tiene restricción UNIQUE para evitar duplicados.
     */
    @Entity
    @Table(name = "persona", uniqueConstraints = {
            @UniqueConstraint(columnNames = {"clave_persona"})
    })
    public class Person {

        //Identificador autogenerado en la BD 
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /**
         * Clave única calculada (SHA-256 en hex). Se genera por la aplicación
         * y se almacena para detectar duplicados.
         */
        @Column(name = "clave_persona", nullable = false, unique = true, length = 128)
        private String clavePersona;

        //Nombre (campo extraído por OCR) 
        @Column(name = "nombre", nullable = false)
        private String nombre;

        //Apellido (campo extraído por OCR) 
        @Column(name = "apellido", nullable = false)
        private String apellido;

        //Fecha de nacimiento en formato simplificado YYYY-MM-DD 
        @Column(name = "fecha_nacimiento")
        private String fechaNacimiento;

        //CURP extraída por OCR 
        @Column(name = "curp", unique = true, length = 18)
        private String curp;

        public Person() {
        }

        //Constructor de conveniencia para crear objetos desde el OCR 
        public Person(String nombre, String apellido, String fechaNacimiento) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.fechaNacimiento = fechaNacimiento;
        }

        ///Constructor que incluye CURP 
        public Person(String nombre, String apellido, String fechaNacimiento, String curp) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.fechaNacimiento = fechaNacimiento;
            this.curp = curp;
        }

        public Long getId() {
            return id;
        }

        public String getClavePersona() {
            return clavePersona;
        }

        public void setClavePersona(String clavePersona) {
            this.clavePersona = clavePersona;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public String getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(String fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public String getCurp() {
            return curp;
        }

        public void setCurp(String curp) {
            this.curp = curp;
        }

        @Override
        public String toString() {
            return "Persona{" +
                    "id=" + id +
                    ", clavePersona='" + clavePersona + '\'' +
                    ", nombre='" + nombre + '\'' +
                    ", apellido='" + apellido + '\'' +
                    ", fechaNacimiento='" + fechaNacimiento + '\'' +
                    ", curp='" + curp + '\'' +
                    '}';
        }
    }
