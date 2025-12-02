FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build
COPY pom.xml .
COPY .mvn .mvn
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

# Instalar dependencias del sistema (añadimos python3-venv y python3-dev)
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-venv \
    python3-dev \
    tesseract-ocr \
    libsm6 \
    libxext6 \
    libxrender-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar aplicación Java
COPY --from=builder /build/target/*.jar app.jar

# Copiar código Python
COPY PythonCode /app/PythonCode

# Crear carpeta de salida
RUN mkdir -p /app/pasos_procesamiento

# Instalar dependencias Python: actualizar pip y usar opencv-python-headless para evitar compilar opencv
RUN python3 -m venv /opt/venv && \
    /opt/venv/bin/python -m pip install --upgrade pip setuptools wheel && \
    /opt/venv/bin/pip install --no-cache-dir flask opencv-python-headless pytesseract numpy

# Asegurar que el entorno virtual esté en PATH en tiempo de ejecución
ENV PATH="/opt/venv/bin:$PATH"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080 5000

# Iniciar ambas aplicaciones
CMD ["sh", "-c", "java -jar app.jar & cd /app/PythonCode && python3 app.py && wait"]