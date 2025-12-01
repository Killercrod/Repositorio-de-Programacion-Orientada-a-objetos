package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.OCRData;
import com.example.demo.repository.OCRDataRepository;

/**
 * Servicio de negocio para OCRData.
 * 
 * Responsabilidades:
 * - Validar que curp_detectado sea único
 * - Rechazar duplicados con excepción apropiada
 * - Guardar o actualizar registros de OCR
 * - Manejo de excepciones de integridad
 */
@Service
@Transactional
public class OCRDataService {

    @Autowired
    private OCRDataRepository ocrDataRepository;

    /**
     * Guarda un nuevo registro OCRData validando que el CURP sea único.
     * 
     * @param ocrData datos a guardar
     * @return OCRData guardado
     * @throws IllegalArgumentException si el CURP ya existe
     */
    public OCRData guardarOCRData(OCRData ocrData) {
        if (ocrData.getCurpDetectado() == null || ocrData.getCurpDetectado().trim().isEmpty()) {
            throw new IllegalArgumentException("curp_detectado no puede estar vacío");
        }

        // Verificar si ya existe un registro con este CURP
        if (ocrDataRepository.existsByCurpDetectado(ocrData.getCurpDetectado().trim())) {
            throw new IllegalArgumentException(
                "Ya existe un registro con CURP: " + ocrData.getCurpDetectado() + 
                ". El CURP debe ser único."
            );
        }

        // Si no existe, guardar el nuevo registro
        return ocrDataRepository.save(ocrData);
    }

    /**
     * Busca un registro por CURP detectado.
     * 
     * @param curpDetectado el CURP a buscar
     * @return Optional con el registro si existe
     */
    public Optional<OCRData> obtenerPorCurp(String curpDetectado) {
        if (curpDetectado == null || curpDetectado.trim().isEmpty()) {
            return Optional.empty();
        }
        return ocrDataRepository.findByCurpDetectado(curpDetectado.trim());
    }

    /**
     * Actualiza un registro OCRData existente.
     * No permite cambiar el CURP (es la clave única).
     * 
     * @param id ID del registro a actualizar
     * @param ocrDataActualizado datos a actualizar
     * @return OCRData actualizado
     * @throws IllegalArgumentException si no existe el registro o se intenta cambiar el CURP
     */
    @Transactional
    public OCRData actualizarOCRData(Long id, OCRData ocrDataActualizado) {
        Optional<OCRData> existente = ocrDataRepository.findById(id);
        
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe registro OCR con ID: " + id);
        }

        OCRData original = existente.get();

        // No permitir cambio de CURP (es la clave única)
        if (!original.getCurpDetectado().equals(ocrDataActualizado.getCurpDetectado())) {
            throw new IllegalArgumentException(
                "No se puede cambiar el CURP de un registro existente. " +
                "CURP original: " + original.getCurpDetectado() +
                ", nuevo CURP: " + ocrDataActualizado.getCurpDetectado()
            );
        }

        // Actualizar campos permitidos
        original.setDocumentoIdentificado(ocrDataActualizado.getDocumentoIdentificado());
        original.setNombre(ocrDataActualizado.getNombre());
        original.setApellido(ocrDataActualizado.getApellido());
        original.setApellidoMaterno(ocrDataActualizado.getApellidoMaterno());
        original.setFechaNacimiento(ocrDataActualizado.getFechaNacimiento());
        original.setConfianza(ocrDataActualizado.getConfianza());
        original.setConfianzaValor(ocrDataActualizado.getConfianzaValor());
        original.setDireccion(ocrDataActualizado.getDireccion());
        original.setDatosAdicionales(ocrDataActualizado.getDatosAdicionales());

        return ocrDataRepository.save(original);
    }

    /**
     * Elimina un registro por ID.
     * 
     * @param id ID del registro a eliminar
     */
    public void eliminarOCRData(Long id) {
        if (!ocrDataRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe registro OCR con ID: " + id);
        }
        ocrDataRepository.deleteById(id);
    }

    /**
     * Obtiene un registro por ID.
     * 
     * @param id ID del registro
     * @return Optional con el registro si existe
     */
    public Optional<OCRData> obtenerPorId(Long id) {
        return ocrDataRepository.findById(id);
    }
}
