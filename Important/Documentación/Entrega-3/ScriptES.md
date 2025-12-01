Primera:
# 📌 Guión Comparativo – Evolución del Proyecto (≈2 minutos)

## 🎤 Presentación General
**"Buenos días. Hoy explicaremos cómo evolucionó nuestro proyecto desde la primera entrega hasta la segunda, destacando las mejoras técnicas y funcionales más importantes."**

---

## 1. De un prototipo de consola a un sistema web completo (30 segundos)

En la primera entrega, presentamos un prototipo funcional que operaba completamente a través de la consola.

El sistema:

- Captura de video con OpenCV
- Procesamiento de imágenes con filtros (escala de grises, desenfoque, umbralización adaptativa)
- Extracción de texto con Tesseract
- Validación de campos clave como **Nombre, CURP, Dirección y Fecha de Nacimiento**

Era una herramienta sólida, pero limitada a un flujo técnico sin una interfaz visual ni persistencia de datos.

En la segunda entrega, dimos un gran paso adelante:
✔ Desarrollamos una **interfaz web profesional** con HTML, CSS y JavaScript.
✔ Mejoramos la experiencia de usuario.
✔ El sistema se volvió accesible sin depender de la ejecución en consola.

---

## 2. Evolución de la arquitectura: de un script monolítico a MVC con persistencia (35 s)

En la primera entrega, la mayor parte de la lógica residía en un único flujo de trabajo: captura de vídeo, preprocesamiento, OCR y validación.

En la segunda entrega, implementamos completamente el **patrón MVC**:

- **Modelo:** Entidades de Hibernate como `INE` y `Usuario`
- **Vista:** Interfaces web HTML + JavaScript
- **Controlador:** Servlets que gestionan peticiones HTTP

También integramos una **base de datos H2** con Hibernate, lo que nos permitió:

- Persistencia de registros
- Cumplimiento de requisitos como **RNF-02 (prevención de duplicados)**
- Soporte de un flujo completo de vista → controlador → modelo persistente

---

## 3. Requisitos: de básicos a refinados y totalmente trazables (30 s)

Primera entrega → Requisitos funcionales mínimos: captura, procesamiento e identificación de campos.

Segunda entrega → Se refinaron y formalizaron los requisitos:

- Historias de usuario con criterios de aceptación
- Una clase `CURPValidator` directamente vinculada a RF-03
- Priorización mediante el método **MoSCoW**:
- OBLIGATORIO: Validación en tiempo real
- DEBERÍA: Interfaz web adaptable
- PODRÍA: Persistencia de la base de datos
- **Diagrama de clases** actualizado, incluyendo herencia y composición
- Un proceso integral con trazabilidad completa en todas las capas

---

## 4. Proceso y competencias (25 s)

Primera entrega → Centrada principalmente en el procesamiento y reconocimiento de imágenes.

Segunda entrega → Ampliación a ingeniería de software completa:

- Patrón MVC
- Hibernate y persistencia de datos
- Ingeniería de requisitos
- Control de versiones y colaboración a través de GitHub
- Sprints semanales
- Seguimiento de métricas de contribución mediante GitHub Projects

Esto fortaleció competencias como el trabajo en equipo, la documentación técnica y el diseño arquitectónico.

---

## 🎯 Conclusión (10 s)

**"Es importante aclarar que desde la planificación del proyecto hasta la implementación de la primera entrega, este no experimentó cambios significativos, ya que el producto principal desde el principio fue el propio script de Python. Fue solo a partir de la segunda entrega que comenzamos a planificar e implementar una interfaz de usuario con HTML."**

## Segunda parte:

En esta segunda fase, nuestro proyecto evolucionó significativamente en comparación con la primera entrega. Lo que comenzó como un prototipo básico de consola es ahora un sistema mucho más sólido gracias a tres mejoras importantes: desarrollamos una interfaz web profesional, implementamos completamente la arquitectura MVC e integramos una base de datos H2 con Hibernate para lograr una persistencia de datos real.

Durante esta fase, también tuvimos que replantear varias ideas. Inicialmente, planeamos mantener todos los datos en memoria, pero esto no permitía una validación fiable, por lo que migramos todo el modelo a Hibernate. Nuestro diagrama de clases también cambió: pasamos de una estructura simple a un modelo más completo con entidades como INE, Usuario, Documento y validadores especializados como CURPValidator, creados para satisfacer requisitos funcionales específicos.

El flujo de trabajo ahora está completamente integrado: la vista HTML envía datos al controlador de servlets, que procesa la lógica, y el modelo valida y almacena la información, evitando duplicados según el requisito no funcional RNF-02. Algunas ideas no funcionaron como se esperaba, como la integración del reconocimiento de imágenes reales en esta fase. Debido a limitaciones de tiempo, pospusimos esta función, pero la arquitectura ya está preparada para ello.

En cuanto a la gestión del proyecto, refinamos nuestros requisitos utilizando el método MoSCoW, priorizando la validación en tiempo real como imprescindible y una interfaz responsiva como imprescindible. Organizamos nuestro trabajo en sprints semanales y realizamos un seguimiento completo en GitHub, lo que nos ayudó a medir las contribuciones y a mantener la transparencia.

En conclusión, esta fase fortaleció tanto nuestras habilidades técnicas en MVC e Hibernate como nuestras competencias de trabajo en equipo. Si bien ajustamos varias ideas sobre la marcha, logramos un sistema más escalable y organizado, alineado con los requisitos reales del proyecto.

## Tercera Parte

Estábamos pensando en implementar una nueva función para el proyecto, ya que queríamos que el usuario pudiera subir un archivo JPG o PNG con su documento de identidad. En lugar de tomar una foto, el programa usaría el archivo subido.
Durante el proceso de tomar la foto, también añadimos referencias visuales para que el usuario supiera en qué espacio y en qué posición debía colocar su documento de identidad para que la imagen se procesara correctamente.
Aunque la idea original del proyecto era centrarse en dispositivos de escritorio, decidimos centrarnos ahora en dispositivos móviles, ya que consideramos que tomar una foto resulta mucho más natural en un dispositivo móvil que con la cámara de un ordenador.

## Parte final
Simplemente explicamos la función del programa y mostramos el ejemplo.
