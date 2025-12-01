# 📌 Guion Comparativo – Evolución del Proyecto (≈2 minutos)

## 🎤 Presentación General
**"Buenos días. Hoy voy a mostrar cómo evolucionó nuestro proyecto desde la primera entrega hasta la segunda, destacando las mejoras más importantes a nivel técnico y funcional."**

---

## 1. De un prototipo en consola a un sistema web completo (30s)

En la primera entrega presentamos un prototipo funcional que operaba completamente en consola.  
El sistema:

- Capturaba video con OpenCV  
- Procesaba imágenes con filtros (grises, blur, umbralización)  
- Extraía texto con Tesseract  
- Validaba campos como **Nombre, CURP, Domicilio y Fecha de Nacimiento**  

Era una herramienta sólida, pero limitada a un flujo técnico sin interfaz visual ni persistencia.

En la segunda entrega dimos un salto importante:  
✔ Migramos a una **interfaz web profesional** hecha con HTML, CSS y JavaScript  
✔ Mejoramos la experiencia del usuario  
✔ Hicimos el sistema accesible sin depender de consola

---

## 2. Evolución Arquitectónica: de script monolítico a MVC con persistencia (35s)

En la primera entrega, casi toda la lógica vivía en un solo flujo: captura, filtros, OCR y validación.

En la segunda entrega implementamos completamente el **patrón MVC**:

- **Modelo:** entidades Hibernate como `INE` y `Usuario`  
- **Vista:** interfaces web HTML + JS  
- **Controlador:** Servlets que gestionan peticiones HTTP  

Además, integramos una **base de datos H2** usando Hibernate, lo que permitió:

- Persistir datos  
- Cumplir requisitos como el **RNF-02 (prevención de duplicados)**  
- Tener un flujo completo desde la vista → controlador → modelo persistente

---

## 3. Requisitos: de básicos a refinados y completamente trazables (30s)

Primera entrega → Requisitos mínimos: captura, procesamiento e identificación de campos esenciales.

Segunda entrega → Refinamos y formalizamos requisitos:

- Historias de usuario con criterios de aceptación  
- ValidadorCURP ligado directamente al RF-03  
- Priorización usando **MoSCoW**:  
  - MUST: Validación en tiempo real  
  - SHOULD: Interfaz responsive  
  - COULD: Persistencia  
- Actualización del **diagrama de clases**, ahora con herencia y composición  
- Flujo 100% trazable entre todas las capas

---

## 4. Proceso y Competencias (25s)

Primera entrega → Enfoque técnico en procesamiento de imágenes.

Segunda entrega → Enfoque completo en ingeniería de software:

- MVC  
- Hibernate  
- Gestión de requisitos  
- Control de versiones en GitHub  
- Sprints de 1 semana  
- Métricas de participación rastreables en GitHub Projects  

Fortalecimos competencias como trabajo en equipo, documentación técnica y diseño arquitectónico.

---

## 🎯 Conclusión (10s)

**"Es importante aclarar que desde la planeación del proyecto hasta la implementación del mismo en la primer entrega el proyecto no tuvo cambios significantes ya que desde un inicio el proyecto en si era el scrpt de python, que luego a partir de la segunda entrega se comenzo a planear la implementacion de un UI con html."**
