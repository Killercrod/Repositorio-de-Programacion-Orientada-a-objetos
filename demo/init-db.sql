-- ============================================================
-- Script SQL para inicializar la base de datos INE
-- Compatible con MySQL 8.0+
-- ============================================================

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS ine_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ine_db;

-- ============================================================
-- Tabla: ocr_data
-- Descripción: Almacena datos OCR extraídos de documentos INE
-- Prioridad: curp_detectado (UNIQUE)
-- ============================================================
CREATE TABLE IF NOT EXISTS ocr_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Identificación del documento
    documento_identificado BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_documento VARCHAR(50) NOT NULL DEFAULT 'INE',
    
    -- CURP - Campo prioritario (UNIQUE)
    curp_detectado VARCHAR(18) NOT NULL UNIQUE,
    
    -- Datos personales
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    apellido_materno VARCHAR(100),
    fecha_nacimiento VARCHAR(20),
    
    -- Confianza del OCR
    confianza VARCHAR(20),
    confianza_valor DECIMAL(3, 2),
    
    -- Información adicional
    direccion LONGTEXT,
    datos_adicionales JSON,
    
    -- Marcas de tiempo
    fecha_procesamiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Índices para búsquedas rápidas
    INDEX idx_curp_detectado (curp_detectado),
    INDEX idx_fecha_procesamiento (fecha_procesamiento),
    UNIQUE KEY uk_curp_detectado (curp_detectado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: persona (Legacy - para compatibilidad)
-- Descripción: Almacena personas con clave única
-- ============================================================
CREATE TABLE IF NOT EXISTS persona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Clave única generada (SHA-256 hex)
    clave_persona VARCHAR(128) NOT NULL UNIQUE,
    
    -- Datos básicos
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    fecha_nacimiento VARCHAR(20),
    
    -- CURP (opcional)
    curp VARCHAR(18) UNIQUE,
    
    -- Índices
    INDEX idx_clave_persona (clave_persona),
    INDEX idx_curp (curp),
    UNIQUE KEY uk_clave_persona (clave_persona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Vista: v_ocr_data_activos
-- Descripción: Muestra registros OCR activos (identificados correctamente)
-- ============================================================
CREATE OR REPLACE VIEW v_ocr_data_activos AS
SELECT 
    id,
    curp_detectado,
    nombre,
    apellido,
    apellido_materno,
    fecha_nacimiento,
    tipo_documento,
    confianza,
    confianza_valor,
    fecha_procesamiento
FROM ocr_data
WHERE documento_identificado = TRUE
ORDER BY fecha_procesamiento DESC;

-- ============================================================
-- Vista: v_duplicados_potenciales
-- Descripción: Identifica potenciales duplicados por CURP
-- ============================================================
CREATE OR REPLACE VIEW v_duplicados_potenciales AS
SELECT 
    curp_detectado,
    COUNT(*) as cantidad,
    GROUP_CONCAT(DISTINCT id ORDER BY id) as ids
FROM ocr_data
GROUP BY curp_detectado
HAVING COUNT(*) > 1;

-- ============================================================
-- Procedimiento: sp_limpiar_datos_ocr
-- Descripción: Limpia datos OCR antiguos (más de 90 días)
-- ============================================================
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_limpiar_datos_ocr(
    IN dias_antiguedad INT DEFAULT 90
)
BEGIN
    DELETE FROM ocr_data
    WHERE DATE(fecha_procesamiento) <= DATE_SUB(CURDATE(), INTERVAL dias_antiguedad DAY)
    AND documento_identificado = FALSE;
    
    SELECT ROW_COUNT() AS registros_eliminados;
END //
DELIMITER ;

-- ============================================================
-- Procedimiento: sp_reportar_estadisticas_ocr
-- Descripción: Genera reporte de estadísticas OCR
-- ============================================================
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_reportar_estadisticas_ocr()
BEGIN
    SELECT 
        COUNT(*) as total_registros,
        SUM(CASE WHEN documento_identificado = TRUE THEN 1 ELSE 0 END) as identificados,
        SUM(CASE WHEN documento_identificado = FALSE THEN 1 ELSE 0 END) as no_identificados,
        MIN(fecha_procesamiento) as fecha_primer_registro,
        MAX(fecha_procesamiento) as fecha_ultimo_registro,
        AVG(COALESCE(confianza_valor, 0)) as confianza_promedio
    FROM ocr_data;
END //
DELIMITER ;

-- ============================================================
-- Índices adicionales para optimización
-- ============================================================
CREATE INDEX idx_ocr_documento_identificado ON ocr_data(documento_identificado);
CREATE INDEX idx_ocr_tipo_documento ON ocr_data(tipo_documento);
CREATE INDEX idx_ocr_confianza_valor ON ocr_data(confianza_valor);

-- ============================================================
-- Datos de prueba (opcional)
-- ============================================================
-- INSERT INTO ocr_data 
-- (documento_identificado, tipo_documento, curp_detectado, nombre, apellido, fecha_nacimiento, confianza, confianza_valor)
-- VALUES 
-- (TRUE, 'INE', 'XXXX990101HDFXXX00', 'Juan', 'Pérez', '1999-01-01', 'alta', 0.95);

COMMIT;

-- ============================================================
-- Verificación final
-- ============================================================
SHOW TABLES;
SHOW CREATE TABLE ocr_data;
