# Migración a Java 21 + MySQL + Hibernate (Spring Data JPA)

## Resumen de Cambios

Se ha completado la migración de la aplicación OCR INE a:
- **Java 21** (LTS - versión más reciente recomendada)
- **Spring Boot 3.5.6**
- **Hibernate 6.6.29** (JPA 3.x con jakarta.persistence)
- **MySQL 8.0+**
- **Spring Data JPA**

### Prioridad: CURP Detectado

La arquitectura está diseñada con **curp_detectado** como identificador único principal:
- UNIQUE constraint en la BD
- Validación en el servicio (OCRDataService)
- Rechazo de duplicados con DuplicatePersonException

---

## Configuración de la Base de Datos

### 1. Crear la BD automáticamente

La aplicación utilizará Hibernate con `ddl-auto: update` para crear/actualizar tablas automáticamente:

```properties
spring.jpa.hibernate.ddl-auto=update  # En application.properties
```

**O** ejecutar manualmente el script SQL:

```bash
mysql -u root -p < init-db.sql
```

### 2. Configurar `application.properties`

Edita `src/main/resources/application.properties`:

```properties
# Base de Datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/ine_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=tu_contraseña_mysql

# Hibernate JPA
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## Estructura de Clases

### Entidades

#### `OCRData.java`
Mapea JSON desde Python con todos los campos OCR:
- `curp_detectado` (UNIQUE, obligatorio)
- `nombre`, `apellido`, `apellido_materno`
- `fecha_nacimiento`
- `confianza`, `confianza_valor`
- Timestamps: `fecha_procesamiento`, `fecha_actualizacion`

#### `Person.java` (Legacy)
Entidad heredada compatible con la anterior:
- `clavePersona` (SHA-256)
- Campos básicos de persona

### Repositorios

#### `OCRDataRepository`
Spring Data JPA repository con métodos:
- `findByCurpDetectado(String curp)` - Búsqueda por CURP
- `existsByCurpDetectado(String curp)` - Verificar duplicados

### Servicios

#### `OCRDataService`
Lógica de negocio:
- `guardarOCRData(OCRData)` - Valida CURP único, rechaza duplicados
- `actualizarOCRData(Long id, OCRData)` - Actualiza sin cambiar CURP
- `obtenerPorCurp(String curp)` - Buscar por CURP
- Manejo de excepciones: `IllegalArgumentException` para duplicados

### Controladores

#### `OCRDataController`
Endpoints REST:
- **POST** `/api/ocr-data` - Crear registro OCR
  ```json
  {
    "documento_identificado": true,
    "tipo_documento": "INE",
    "curp_detectado": "XXXX990101HDFXXX00",
    "nombre": "Juan",
    "apellido": "Pérez",
    "fecha_nacimiento": "1999-01-01"
  }
  ```
  
  Respuesta (éxito - 201):
  ```json
  {
    "estado": "EXITO",
    "mensaje": "Datos OCR guardados correctamente",
    "datos": { ... }
  }
  ```
  
  Respuesta (duplicado - 409):
  ```json
  {
    "estado": "DUPLICADO",
    "mensaje": "Ya existe un registro con CURP: XXXX..."
  }
  ```

- **GET** `/api/ocr-data/{id}` - Obtener por ID
- **GET** `/api/ocr-data/curp/{curpDetectado}` - Obtener por CURP
- **PUT** `/api/ocr-data/{id}` - Actualizar registro
- **DELETE** `/api/ocr-data/{id}` - Eliminar registro

#### `CapturaController`
Endpoint mejorado que integra con Python:
- **GET** `/pycode` - Captura desde Python y guarda en BD

Flujo:
1. Envía solicitud a `http://localhost:5000/tomar-foto` (Python Flask)
2. Recibe JSON con datos OCR
3. Extrae `curp_detectado` (prioritario)
4. Valida si CURP es único
5. Guarda en BD MySQL mediante `OCRDataService`
6. Devuelve respuesta con estado

---

## Compilación y Ejecución

### Compilar

```bash
cd demo
./mvnw clean compile
```

### Empaquetar

```bash
./mvnw clean package -DskipTests
```

Genera: `target/demo-1.0.0.jar`

### Ejecutar

```bash
# Con Maven
./mvnw spring-boot:run

# O con Java directamente
java -jar target/demo-1.0.0.jar
```

La aplicación estará disponible en: `http://localhost:8080`

---

## Flujo de Integración Python ↔ Java

```
┌─────────────┐
│  Python OCR │ (pycode.py)
│  (Tesseract)│
└──────┬──────┘
       │ JSON con curp_detectado
       ▼
┌──────────────────────┐
│ CapturaController    │ (/pycode)
│ OCRDataService       │
└──────┬───────────────┘
       │ Valida CURP único
       ▼
┌──────────────────────┐
│ MySQL Database       │
│ Tabla: ocr_data      │
│ PK: curp_detectado   │
└──────────────────────┘
```

---

## Manejo de Duplicados

Cuando se intenta insertar un CURP que ya existe:

1. **Validación en Servicio**: `OCRDataService.guardarOCRData()`
2. **Query a BD**: `SELECT * FROM ocr_data WHERE curp_detectado = ?`
3. **Resultado**:
   - Si existe → Lanza `IllegalArgumentException` con mensaje descriptivo
   - Si no existe → Guarda el registro

4. **Response HTTP**: 409 Conflict
   ```json
   {
     "estado": "DUPLICADO",
     "mensaje": "Ya existe un registro con CURP: XXXX990101HDFXXX00. El CURP debe ser único."
   }
   ```

---

## Dependencias Maven Agregadas

```xml
<!-- Spring Data JPA + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Connector -->
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

<!-- Jackson JSON -->
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

---

## Versión de Java 21

### Cambios en Imports

Se cambió de `javax.persistence` a `jakarta.persistence`:

```java
// Antes (Java 8-11)
import javax.persistence.*;

// Después (Java 21 + Spring Boot 3.x)
import jakarta.persistence.*;
```

### Características de Java 21 Utilizadas

- Virtual Threads (compatibles)
- Pattern Matching (instanceof)
- Text Blocks (strings multilinea)
- Record Classes (opcionalmente)

---

## Pruebas

### Crear registro OCR

```bash
curl -X POST http://localhost:8080/api/ocr-data \
  -H "Content-Type: application/json" \
  -d '{
    "documento_identificado": true,
    "tipo_documento": "INE",
    "curp_detectado": "XXXX990101HDFXXX00",
    "nombre": "Juan",
    "apellido": "Pérez",
    "fecha_nacimiento": "1999-01-01",
    "confianza": "alta",
    "confianza_valor": 0.95
  }'
```

### Buscar por CURP

```bash
curl http://localhost:8080/api/ocr-data/curp/XXXX990101HDFXXX00
```

### Intentar crear duplicado

```bash
# Mismo CURP → Error 409 Conflict
curl -X POST http://localhost:8080/api/ocr-data \
  -H "Content-Type: application/json" \
  -d '{
    "documento_identificado": true,
    "tipo_documento": "INE",
    "curp_detectado": "XXXX990101HDFXXX00",
    "nombre": "Otro Nombre",
    "apellido": "Otro Apellido",
    "fecha_nacimiento": "1999-01-01"
  }'
# Respuesta: 409 DUPLICADO
```

---

## Troubleshooting

### Error: "Can't connect to MySQL"

```bash
# Verificar que MySQL está corriendo
mysql -u root -p -e "SELECT VERSION();"

# Si no está instalado:
# Windows: Descargar desde https://dev.mysql.com/downloads/mysql/
# macOS: brew install mysql
# Linux: sudo apt-get install mysql-server
```

### Error: "Unknown database 'ine_db'"

```bash
# Ejecutar el script SQL
mysql -u root -p < demo/init-db.sql
```

### Error: "Class definition for java.util.concurrent..."

Este es un warning de Java 21 con Unsafe API. Es seguro ignorarlo.

---

## Siguiente Paso: Desplegar en Producción

Para desplegar en Azure:
1. Crear instancia de Azure Database for MySQL
2. Actualizar `application.properties` con credenciales de Azure
3. Desplegar WAR en Azure App Service o contenedor en Container Apps

---

## Referencia Rápida

| Componente | Ubicación | Responsabilidad |
|-----------|-----------|-----------------|
| OCRData | `model/` | Entidad JPA con mapeo JSON |
| OCRDataRepository | `repository/` | CRUD de BD |
| OCRDataService | `service/` | Lógica de duplicados |
| OCRDataController | `controller/` | Endpoints REST |
| CapturaController | `controller/` | Integración con Python |
| init-db.sql | raíz proyecto | Script de BD |

---

**Migración completada en Java 21 ✅**
