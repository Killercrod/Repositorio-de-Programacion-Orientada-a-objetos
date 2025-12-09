package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.OCRData;

/**
 * Repository de Spring Data JPA para la entidad OCRData.
 * Proporciona operaciones CRUD y consultas personalizadas.
 */
@Repository
public interface OCRDataRepository extends JpaRepository<OCRData, Long> {

    /**
     * Busca un registro por CURP detectado.
     * Prioridad: curp_detectado es UNIQUE en la BD.
     * 
     * @param curpDetectado el CURP a buscar
     * @return Optional con el registro si existe
     */
    Optional<OCRData> findByCurpDetectado(String curpDetectado);

    /**
     * Verifica si existe un registro con el CURP dado.
     * 
     * @param curpDetectado el CURP a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCurpDetectado(String curpDetectado);
}
