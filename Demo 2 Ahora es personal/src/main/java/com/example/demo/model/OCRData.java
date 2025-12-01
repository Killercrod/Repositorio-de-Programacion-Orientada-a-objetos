package com.example.demo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad que mapea los datos JSON enviados desde Python (pycode.py).
 * Prioridad: curp_detectado (UNIQUE)
 * 
 * Campos esperados del JSON:
 * {
 *   "documento_identificado": true,
 *   "tipo_documento": "INE",
 *   "curp_detectado": "XXXX...",
 *   "nombre": "Juan",
 *   "apellido": "Pérez",
 *   "fecha_nacimiento": "1990-01-15",
 *   "confianza": "alta",
 *   "confianza_valor": 0.95
 * }
 */
@Entity
@Table(name = "ocr_data", uniqueConstraints = {
    @UniqueConstraint(columnNames = "curp_detectado", name = "uk_curp_detectado")
})
public class OCRData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_identificado", nullable = false)
    @JsonProperty("documento_identificado")
    private Boolean documentoIdentificado;

    @Column(name = "tipo_documento", nullable = false, length = 50)
    @JsonProperty("tipo_documento")
    private String tipoDocumento;

    @Column(name = "curp_detectado", nullable = false, unique = true, length = 18)
    @JsonProperty("curp_detectado")
    private String curpDetectado;

    @Column(name = "nombre", length = 100)
    @JsonProperty("nombre")
    private String nombre;

    @Column(name = "apellido", length = 100)
    @JsonProperty("apellido")
    private String apellido;

    @Column(name = "apellido_materno", length = 100)
    @JsonProperty("apellido_materno")
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", length = 20)
    @JsonProperty("fecha_nacimiento")
    private String fechaNacimiento;

    @Column(name = "confianza", length = 20)
    @JsonProperty("confianza")
    private String confianza;

    @Column(name = "confianza_valor")
    @JsonProperty("confianza_valor")
    private Double confianzaValor;

    @Column(name = "direccion", columnDefinition = "TEXT")
    @JsonProperty("direccion")
    private String direccion;

    @Column(name = "datos_adicionales", columnDefinition = "JSON")
    @JsonProperty("datos_adicionales")
    private String datosAdicionales;

    @Column(name = "fecha_procesamiento", nullable = false, updatable = false)
    private LocalDateTime fechaProcesamiento;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructores
    public OCRData() {
    }

    public OCRData(String curpDetectado, String nombre, String apellido, String tipoDocumento) {
        this.curpDetectado = curpDetectado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.documentoIdentificado = true;
        this.fechaProcesamiento = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de ciclo de vida JPA
    @PrePersist
    protected void onCreate() {
        this.fechaProcesamiento = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDocumentoIdentificado() {
        return documentoIdentificado;
    }

    public void setDocumentoIdentificado(Boolean documentoIdentificado) {
        this.documentoIdentificado = documentoIdentificado;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getCurpDetectado() {
        return curpDetectado;
    }

    public void setCurpDetectado(String curpDetectado) {
        this.curpDetectado = curpDetectado;
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

    public String getConfianza() {
        return confianza;
    }

    public void setConfianza(String confianza) {
        this.confianza = confianza;
    }

    public Double getConfianzaValor() {
        return confianzaValor;
    }

    public void setConfianzaValor(Double confianzaValor) {
        this.confianzaValor = confianzaValor;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDatosAdicionales() {
        return datosAdicionales;
    }

    public void setDatosAdicionales(String datosAdicionales) {
        this.datosAdicionales = datosAdicionales;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public String toString() {
        return "OCRData{" +
                "id=" + id +
                ", documentoIdentificado=" + documentoIdentificado +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", curpDetectado='" + curpDetectado + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", confianza='" + confianza + '\'' +
                ", confianzaValor=" + confianzaValor +
                ", fechaProcesamiento=" + fechaProcesamiento +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
