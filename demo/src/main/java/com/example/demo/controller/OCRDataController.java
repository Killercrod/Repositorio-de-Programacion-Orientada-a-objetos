package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.OCRData;
import com.example.demo.service.OCRDataService;

/**
 * Controlador REST para manejar datos OCR.
 * 
 * Endpoint principal: POST /api/ocr-data
 * Recibe JSON desde Python (pycode.py) y guarda en BD MySQL.
 * 
 * Ejemplo de JSON esperado:
 * {
 *   "documento_identificado": true,
 *   "tipo_documento": "INE",
 *   "curp_detectado": "XXXX990101HDFXXX00",
 *   "nombre": "Juan",
 *   "apellido": "Pérez",
 *   "apellido_materno": "García",
 *   "fecha_nacimiento": "1999-01-01",
 *   "confianza": "alta",
 *   "confianza_valor": 0.95
 * }
 */
@RestController
@RequestMapping("/api/ocr-data")
@CrossOrigin(origins = "*")
public class OCRDataController {

    @Autowired
    private OCRDataService ocrDataService;

    /**
     * POST /api/ocr-data
     * Recibe datos OCR en formato JSON y los guarda en la BD.
     * 
     * @param ocrData datos OCR a guardar
     * @return ResponseEntity con el registro guardado o error
     */
    @PostMapping
    public ResponseEntity<?> crearOCRData(@RequestBody OCRData ocrData) {
        try {
            if (ocrData.getCurpDetectado() == null || ocrData.getCurpDetectado().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("ERROR", "curp_detectado es obligatorio y no puede estar vacío")
                );
            }

            OCRData guardado = ocrDataService.guardarOCRData(ocrData);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new SuccessResponse("EXITO", "Datos OCR guardados correctamente", guardado)
            );

        } catch (IllegalArgumentException e) {
            // Duplicado de CURP o validación fallida
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse("DUPLICADO", e.getMessage())
            );
        } catch (Exception e) {
            // Error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("ERROR", "Error al guardar datos OCR: " + e.getMessage())
            );
        }
    }

    /**
     * GET /api/ocr-data/{id}
     * Obtiene un registro OCR por ID.
     * 
     * @param id ID del registro
     * @return ResponseEntity con el registro o 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerOCRData(@PathVariable Long id) {
        try {
            Optional<OCRData> ocrData = ocrDataService.obtenerPorId(id);
            if (ocrData.isPresent()) {
                return ResponseEntity.ok(
                    new SuccessResponse("EXITO", "Registro encontrado", ocrData.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponse("NO_ENCONTRADO", "No existe registro con ID: " + id)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("ERROR", e.getMessage())
            );
        }
    }

    /**
     * GET /api/ocr-data/curp/{curpDetectado}
     * Busca un registro por CURP.
     * 
     * @param curpDetectado CURP a buscar
     * @return ResponseEntity con el registro o 404
     */
    @GetMapping("/curp/{curpDetectado}")
    public ResponseEntity<?> obtenerPorCurp(@PathVariable String curpDetectado) {
        try {
            Optional<OCRData> ocrData = ocrDataService.obtenerPorCurp(curpDetectado);
            if (ocrData.isPresent()) {
                return ResponseEntity.ok(
                    new SuccessResponse("EXITO", "Registro encontrado", ocrData.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponse("NO_ENCONTRADO", "No existe registro con CURP: " + curpDetectado)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("ERROR", e.getMessage())
            );
        }
    }

    /**
     * PUT /api/ocr-data/{id}
     * Actualiza un registro OCR existente.
     * 
     * @param id ID del registro a actualizar
     * @param ocrData datos a actualizar
     * @return ResponseEntity con el registro actualizado o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarOCRData(@PathVariable Long id, @RequestBody OCRData ocrData) {
        try {
            OCRData actualizado = ocrDataService.actualizarOCRData(id, ocrData);
            return ResponseEntity.ok(
                new SuccessResponse("EXITO", "Registro actualizado correctamente", actualizado)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse("ERROR_VALIDACION", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("ERROR", e.getMessage())
            );
        }
    }

    /**
     * DELETE /api/ocr-data/{id}
     * Elimina un registro OCR.
     * 
     * @param id ID del registro a eliminar
     * @return ResponseEntity con mensaje de éxito o error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOCRData(@PathVariable Long id) {
        try {
            ocrDataService.eliminarOCRData(id);
            return ResponseEntity.ok(
                new ErrorResponse("EXITO", "Registro eliminado correctamente")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("NO_ENCONTRADO", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("ERROR", e.getMessage())
            );
        }
    }

    /**
     * Clase interna para respuestas de éxito.
     */
    public static class SuccessResponse {
        public String estado;
        public String mensaje;
        public Object datos;

        public SuccessResponse(String estado, String mensaje, Object datos) {
            this.estado = estado;
            this.mensaje = mensaje;
            this.datos = datos;
        }

        public String getEstado() {
            return estado;
        }

        public String getMensaje() {
            return mensaje;
        }

        public Object getDatos() {
            return datos;
        }
    }

    /**
     * Clase interna para respuestas de error.
     */
    public static class ErrorResponse {
        public String estado;
        public String mensaje;

        public ErrorResponse(String estado, String mensaje) {
            this.estado = estado;
            this.mensaje = mensaje;
        }

        public String getEstado() {
            return estado;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
