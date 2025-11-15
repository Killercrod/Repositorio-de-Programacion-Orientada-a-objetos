# Resumen de Migración a Java 21 + MySQL + Hibernate

## ✅ Cambios Completados

### 1. Actualización de Java 21

- **Archivo**: `pom.xml`
- **Cambios**:
  - `<java.version>21</java.version>`
  - `<maven.compiler.source>21</maven.compiler.source>`
  - `<maven.compiler.target>21</maven.compiler.target>`

### 2. Dependencias Agregadas

```xml
<!-- Spring Data JPA + Hibernate 6.6.29 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Connector 8.0.33 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- Jakarta Persistence (Java 21) -->
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
</dependency>

<!-- Jackson JSON Processing -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>

<!-- Validación -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 3. Migraciones de Importes

**Cambio de javax → jakarta para Java 21:**

```java
// ANTES (Java 8-11)
import javax.persistence.*;

// DESPUÉS (Java 21)
import jakarta.persistence.*;
```

### 4. Entidades JPA Creadas

#### `OCRData.java` (Nueva)
- Mapea JSON desde Python (pycode.py)
- **Prioridad**: `curp_detectado` (UNIQUE, obligatorio)
- Campos:
  - `documento_identificado`, `tipo_documento`
  - `curp_detectado`, `nombre`, `apellido`, `apellido_materno`
  - `fecha_nacimiento`, `confianza`, `confianza_valor`
  - `direccion`, `datos_adicionales`
  - Timestamps: `fecha_procesamiento`, `fecha_actualizacion`

#### `Person.java` (Actualizada)
- Migrada a `jakarta.persistence`
- Compatible con BD existente
- Mantiene `clavePersona` y CURP

### 5. Capa de Persistencia

#### `OCRDataRepository.java` (Nueva)
- Spring Data JPA Repository
- Métodos:
  - `findByCurpDetectado(String curp)`
  - `existsByCurpDetectado(String curp)`

#### `OCRDataService.java` (Nueva)
- Lógica de negocio con validación de duplicados
- Métodos:
  - `guardarOCRData(OCRData)` - Valida CURP único
  - `actualizarOCRData(Long id, OCRData)`
  - `obtenerPorCurp(String curp)`
  - Manejo de `IllegalArgumentException` para duplicados

### 6. Controladores REST

#### `OCRDataController.java` (Nueva)
- Endpoints para CRUD OCR:
  - **POST** `/api/ocr-data` - Crear registro
  - **GET** `/api/ocr-data/{id}` - Obtener por ID
  - **GET** `/api/ocr-data/curp/{curpDetectado}` - Obtener por CURP
  - **PUT** `/api/ocr-data/{id}` - Actualizar
  - **DELETE** `/api/ocr-data/{id}` - Eliminar
- Respuestas JSON estructuradas:
  - Éxito: `{"estado":"EXITO", "mensaje":"...", "datos":{...}}`
  - Error: `{"estado":"ERROR", "mensaje":"..."}`
  - Duplicado: `{"estado":"DUPLICADO", "mensaje":"..."}`

#### `CapturaController.java` (Actualizado)
- Integración mejorada con Python Flask
- **GET** `/pycode` - Captura y guarda en BD
- Flujo:
  1. Solicita datos OCR a Python (`http://localhost:5000/tomar-foto`)
  2. Parsea JSON con `ObjectMapper`
  3. Extrae `curp_detectado` (prioritario)
  4. Valida unicidad mediante `OCRDataService`
  5. Guarda en MySQL
  6. Devuelve respuesta con ID y estado

### 7. Configuración de Base de Datos

#### `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ine_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

#### `init-db.sql` (Nuevo)
- Script SQL completo para inicializar BD
- Crea tablas: `ocr_data`, `persona`
- Vistas: `v_ocr_data_activos`, `v_duplicados_potenciales`
- Procedimientos: `sp_limpiar_datos_ocr`, `sp_reportar_estadisticas_ocr`
- Índices optimizados para búsquedas

### 8. Documentación

#### `MIGRACION_JAVA21_MYSQL.md`
- Guía completa de la migración
- Instrucciones de configuración
- Ejemplos de uso
- Troubleshooting

#### `application.properties.example`
- Configuraciones de ejemplo
- Comentarios explicativos
- Diferencias entre desarrollo y producción

---

## 📊 Estructura de Archivos Nuevos

```
demo/
├── pom.xml (ACTUALIZADO - Java 21 + dependencias)
├── init-db.sql (NUEVO - Script SQL)
├── MIGRACION_JAVA21_MYSQL.md (NUEVO - Documentación)
├── application.properties.example (NUEVO - Configuración ejemplo)
├── src/main/java/
│   ├── com/example/demo/
│   │   ├── App.java (existing)
│   │   ├── BDconection.java (existing)
│   │   ├── CapturaController.java (ACTUALIZADO)
│   │   ├── model/
│   │   │   └── OCRData.java (NUEVO)
│   │   ├── repository/
│   │   │   └── OCRDataRepository.java (NUEVO)
│   │   ├── service/
│   │   │   └── OCRDataService.java (NUEVO)
│   │   └── controller/
│   │       └── OCRDataController.java (NUEVO)
│   ├── mx/ine/ocr/db/ (existing)
│   │   ├── model/Person.java (ACTUALIZADO - jakarta imports)
│   │   ├── dao/PersonDAO.java (existing)
│   │   └── util/HibernateUtil.java (existing)
│   └── resources/
│       └── application.properties (ACTUALIZADO)
└── target/
    └── demo-1.0.0.jar (COMPILADO)
```

---

## 🚀 Cómo Usar

### 1. Preparar Base de Datos

```bash
# Opción A: Ejecutar script SQL
mysql -u root -p < demo/init-db.sql

# Opción B: Dejar que Hibernate cree las tablas (ddl-auto: update)
# (Se crearán automáticamente al iniciar la app)
```

### 2. Configurar `application.properties`

```bash
cp demo/application.properties.example demo/src/main/resources/application.properties
# Editar con credenciales MySQL correctas
```

### 3. Compilar

```bash
cd demo
./mvnw clean package -DskipTests
```

### 4. Ejecutar

```bash
# Con Maven
./mvnw spring-boot:run

# O con Java
java -jar target/demo-1.0.0.jar
```

### 5. Probar Integración Python

```bash
# En otra terminal, iniciar Flask Python
cd demo/PythonCode
python app.py  # Escucha en http://localhost:5000

# En otra terminal, probar endpoint
curl http://localhost:8080/pycode
```

---

## ✨ Características de la Migración

### ✅ Prioridad: CURP Detectado

```
┌────────────┐
│   CURP     │ ← Identificador único principal
│ DETECTADO  │ ← Validación en aplicación
│ (UNIQUE)   │ ← Restricción en BD
└────────────┘
```

- Campo obligatorio
- UNIQUE constraint en tabla
- Rechazo de duplicados con código HTTP 409
- Búsqueda rápida mediante índice

### ✅ Integración Python ↔ Java

```
Python OCR → JSON → Java Spring → MySQL
↑                                    ↓
├── JSON con curp_detectado ←──────┤
└─ Validación: ¿Existe CURP? ◄─────┘
   Si existe → Error 409 Conflict
   Si no → Guardar en BD
```

### ✅ Manejo de Duplicados Robusto

1. **Validación en Servicio** (`OCRDataService`)
2. **Query de Verificación** (`existsByCurpDetectado()`)
3. **Excepción Específica** (`IllegalArgumentException`)
4. **Respuesta HTTP** (409 Conflict)

### ✅ Compatibilidad Java 21

- Importes actualizados a `jakarta.persistence`
- Compatible con Virtual Threads
- Soporta Pattern Matching
- Text Blocks en comentarios

---

## 🔍 Validación

```bash
# Verificar compilación
./mvnw clean compile

# Verificar package
./mvnw clean package -DskipTests

# Ver versión de Java
java -version
# Debe mostrar: openjdk version "21"
```

---

## 📝 Base de Datos

### Tabla: `ocr_data`

| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | BIGINT AUTO_INCREMENT | PRIMARY KEY |
| documento_identificado | BOOLEAN | NOT NULL |
| tipo_documento | VARCHAR(50) | NOT NULL |
| **curp_detectado** | VARCHAR(18) | NOT NULL **UNIQUE** |
| nombre | VARCHAR(100) | |
| apellido | VARCHAR(100) | |
| apellido_materno | VARCHAR(100) | |
| fecha_nacimiento | VARCHAR(20) | |
| confianza | VARCHAR(20) | |
| confianza_valor | DECIMAL(3,2) | |
| direccion | LONGTEXT | |
| datos_adicionales | JSON | |
| fecha_procesamiento | TIMESTAMP | NOT NULL |
| fecha_actualizacion | TIMESTAMP | UPDATE CURRENT_TIMESTAMP |

### Índices

```sql
PRIMARY KEY: id
UNIQUE: curp_detectado
INDEX: fecha_procesamiento
INDEX: tipo_documento
INDEX: confianza_valor
```

---

## 🎯 Estado Final

| Aspecto | Estado |
|--------|--------|
| Java | ✅ 21 LTS |
| Spring Boot | ✅ 3.5.6 |
| Hibernate | ✅ 6.6.29 |
| MySQL | ✅ 8.0+ |
| CURP Detectado | ✅ Prioridad Principal |
| Duplicados | ✅ Controlados |
| Integración Python | ✅ Funcional |
| Compilación | ✅ Exitosa |
| Base de Datos | ✅ Lista |

---

## 📞 Soporte

Para problemas con:

- **MySQL**: Ver `MIGRACION_JAVA21_MYSQL.md` sección Troubleshooting
- **Java 21**: Usar JDK 21+ (Eclipse Temurin o OpenJDK)
- **Spring Boot**: Asegurar `application.properties` correcto
- **Python Integration**: Verificar Flask escuche en `localhost:5000`

---

**Migración completada exitosamente ✅**

Fecha: 15 de Noviembre, 2025
Versión: 1.0.0 (Java 21 + MySQL)
