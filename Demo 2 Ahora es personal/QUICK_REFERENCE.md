# Referencia Rápida: Migración Java 21 + Hibernate + MySQL

## 🚀 Inicio Rápido

```bash
# 1. Crear BD MySQL
mysql -u root -p < demo/init-db.sql

# 2. Compilar
cd demo
./mvnw clean package -DskipTests

# 3. Ejecutar
./mvnw spring-boot:run

# 4. Probar
curl http://localhost:8080/api/ocr-data -X GET
```

## 📝 Endpoints Principales

### Crear OCR Data (POST)
```bash
curl -X POST http://localhost:8080/api/ocr-data \
  -H "Content-Type: application/json" \
  -d '{
    "documento_identificado": true,
    "tipo_documento": "INE",
    "curp_detectado": "XXXX990101HDFXXX00",
    "nombre": "Juan",
    "apellido": "Pérez",
    "fecha_nacimiento": "1999-01-01"
  }'
```

**Respuesta (201 Created):**
```json
{
  "estado": "EXITO",
  "mensaje": "Datos OCR guardados correctamente",
  "datos": {
    "id": 1,
    "curp_detectado": "XXXX990101HDFXXX00",
    "nombre": "Juan",
    ...
  }
}
```

**Si CURP duplicado (409 Conflict):**
```json
{
  "estado": "DUPLICADO",
  "mensaje": "Ya existe un registro con CURP: XXXX990101HDFXXX00. El CURP debe ser único."
}
```

### Obtener por CURP (GET)
```bash
curl http://localhost:8080/api/ocr-data/curp/XXXX990101HDFXXX00
```

### Obtener por ID (GET)
```bash
curl http://localhost:8080/api/ocr-data/1
```

### Actualizar (PUT)
```bash
curl -X PUT http://localhost:8080/api/ocr-data/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Carlos",
    "apellido": "Pérez García"
  }'
```

### Eliminar (DELETE)
```bash
curl -X DELETE http://localhost:8080/api/ocr-data/1
```

## 🔐 Flujo de Duplicados

```
┌─────────────────────┐
│ POST /api/ocr-data  │
│ CURP: XXXX990101... │
└──────────┬──────────┘
           │
    ┌──────▼──────┐
    │ Validaciones│
    │ - CURP ≠ ∅  │
    └──────┬──────┘
           │
    ┌──────▼─────────────────────┐
    │ SELECT * WHERE curpDetectado
    │        = 'XXXX990101...'   │
    └──────┬──────────────────────┘
           │
    ┌──────▼──────┐      ┌──────────────┐
    │ ¿Existe?    │      │ Sí, existe   │
    └──────┬──────┘      │ 409 CONFLICT │
           │             └──────────────┘
    No existe
           │
    ┌──────▼──────────┐
    │ INSERT nuevo    │
    │ 201 CREATED     │
    └─────────────────┘
```

## 📊 Estructura Java

```
com.example.demo/
├── model/
│   └── OCRData.java (Entidad JPA)
├── repository/
│   └── OCRDataRepository.java (Spring Data JPA)
├── service/
│   └── OCRDataService.java (Lógica de negocio)
├── controller/
│   ├── OCRDataController.java (REST endpoints)
│   └── CapturaController.java (Integración Python)
└── App.java (Spring Boot main)
```

## 🗄️ SQL Rápido

```sql
-- Ver todos los registros
SELECT * FROM ocr_data ORDER BY fecha_procesamiento DESC;

-- Buscar por CURP
SELECT * FROM ocr_data WHERE curp_detectado = 'XXXX990101HDFXXX00';

-- Contar registros
SELECT COUNT(*) FROM ocr_data;

-- Ver duplicados potenciales
SELECT curp_detectado, COUNT(*) as cantidad
FROM ocr_data
GROUP BY curp_detectado
HAVING COUNT(*) > 1;

-- Últimos 10 registros
SELECT * FROM ocr_data ORDER BY fecha_procesamiento DESC LIMIT 10;

-- Estadísticas
CALL sp_reportar_estadisticas_ocr();
```

## ⚙️ Configuración Esencial

**`application.properties`:**
```properties
# BD
spring.datasource.url=jdbc:mysql://localhost:3306/ine_db
spring.datasource.username=root
spring.datasource.password=

# Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

## 🐛 Troubleshooting

| Error | Solución |
|-------|----------|
| `java.sql.SQLException: Can't connect` | Verificar MySQL está corriendo: `mysql -u root -p -e "SELECT VERSION();"` |
| `Table 'ine_db.ocr_data' doesn't exist` | Ejecutar: `mysql -u root -p < demo/init-db.sql` |
| `java.lang.ClassNotFoundException: jakarta.persistence` | Asegurar JPA en `pom.xml` |
| `Cannot find symbol: class OCRData` | Limpiar Maven: `./mvnw clean compile` |

## 📦 Compilar & Ejecutar

```bash
# Compilar
./mvnw clean compile

# Empaquetar
./mvnw clean package -DskipTests

# Ejecutar JAR
java -jar target/demo-1.0.0.jar

# Ejecutar con Maven
./mvnw spring-boot:run

# Ver logs
tail -f logs/ine-ocr.log
```

## 🔗 Integración Python

**URL Flask esperada:**
```
http://localhost:5000/tomar-foto
```

**Flujo:**
```
1. Frontend → GET /pycode
2. Java → GET http://localhost:5000/tomar-foto
3. Python → Captura + OCR + Devuelve JSON
4. Java → Extrae curp_detectado
5. Java → Valida en BD
6. Java → Guarda o rechaza (409)
7. Frontend ← Respuesta JSON
```

## 📊 JSON Esperado desde Python

```json
{
  "mensaje": "Foto capturada y procesada con éxito",
  "resultado": {
    "documento_identificado": true,
    "tipo_documento": "INE",
    "curp_detectado": "XXXX990101HDFXXX00",
    "nombre": "Juan",
    "apellido": "Pérez",
    "apellido_materno": "García",
    "fecha_nacimiento": "1999-01-01",
    "confianza": "alta",
    "confianza_valor": 0.95,
    "direccion": "Calle Principal 123"
  }
}
```

## 🎯 Campos Importantes

| Campo | Tipo | Obligatorio | Unique | Uso |
|-------|------|-----------|--------|-----|
| curp_detectado | String(18) | ✅ SÍ | ✅ SÍ | Identificador único |
| nombre | String(100) | ❌ NO | ❌ NO | Datos personales |
| apellido | String(100) | ❌ NO | ❌ NO | Datos personales |
| fecha_nacimiento | String(20) | ❌ NO | ❌ NO | Datos personales |
| confianza_valor | Decimal(3,2) | ❌ NO | ❌ NO | Calidad OCR |

## 🔍 Debugging

```bash
# Ver queries SQL
# En application.properties:
spring.jpa.show-sql=true

# Ver parámetros de queries
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Verificar conexión MySQL
mysql -h localhost -u root -p ine_db -e "SELECT 1;"

# Ver tablas creadas
mysql -h localhost -u root -p ine_db -e "SHOW TABLES;"

# Ver estructura tabla
mysql -h localhost -u root -p ine_db -e "DESCRIBE ocr_data;"
```

## 📚 Archivos Importantes

| Archivo | Descripción |
|---------|-----------|
| `pom.xml` | Dependencias Maven |
| `init-db.sql` | Script de BD |
| `MIGRACION_JAVA21_MYSQL.md` | Documentación completa |
| `RESUMEN_MIGRACION.md` | Resumen de cambios |
| `application.properties.example` | Configuración ejemplo |

## 🌐 URL Base

```
http://localhost:8080
```

## ✅ Versiones

- Java: 21 LTS
- Spring Boot: 3.5.6
- Hibernate: 6.6.29
- MySQL: 8.0+
- Maven: 3.9.11

---

**Todos los cambios compilaron exitosamente ✅**
