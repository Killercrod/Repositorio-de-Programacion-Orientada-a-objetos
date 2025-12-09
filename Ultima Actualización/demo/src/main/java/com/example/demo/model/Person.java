package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad JPA que representa una persona en la base de datos.
 * 
 * Notas:
 * - La tabla en la BD se llama "persona".
 * - La columna "clave_persona" almacena el hash único generado a partir
 *   de los datos relevantes (nombre|apellido|fechaNacimiento). Esta
 *   columna tiene restricción UNIQUE para evitar duplicados.
 * - Soporta CURP como identificador único adicional.
 */
@Entity
@Table(name = "persona", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"clave_persona"}, name = "uk_clave_persona"),
        @UniqueConstraint(columnNames = {"curp"}, name = "uk_curp")
})
public class Person {

    // Identificador autogenerado en la BD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Clave única calculada (SHA-256 en hex). Se genera por la aplicación
     * y se almacena para detectar duplicados.
     */
    @Column(name = "clave_persona", nullable = false, unique = true, length = 128)
    private String clavePersona;

    // Nombre (campo extraído por OCR)
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Apellido (campo extraído por OCR)
    @Column(name = "apellido", nullable = false)
    private String apellido;

    // Apellido materno (campo extraído por OCR)
    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    // Fecha de nacimiento en formato simplificado YYYY-MM-DD
    @Column(name = "fecha_nacimiento", length = 20)
    private String fechaNacimiento;

    // CURP extraída por OCR
    @Column(name = "curp", unique = true, length = 18)
    private String curp;

    // Constructores
    public Person() {
    }

    // Constructor de conveniencia para crear objetos desde el OCR
    public Person(String nombre, String apellido, String fechaNacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Constructor que incluye CURP
    public Person(String nombre, String apellido, String fechaNacimiento, String curp) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.curp = curp;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
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
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", curp='" + curp + '\'' +
                '}';
    }
}
