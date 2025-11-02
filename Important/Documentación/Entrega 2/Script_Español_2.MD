 # GUION 2 - SEGUNDA ENTREGA (5 minutos)

## 👤 Omar - Introducción y Evolución del Producto (50 segundos)
"Buenos días a todos. En nuestra primera entrega presentamos un prototipo de consola para identificación de INEs. Para esta segunda entrega, hemos evolucionado significativamente el producto mediante 3 mejoras clave:

Primero, desarrollamos una interfaz web profesional usando HTML, CSS y JavaScript. Segundo, implementamos completamente el patrón MVC, separando claramente modelo, vista y controlador. Y tercero, integramos una base de datos H2 con Hibernate para persistencia robusta de datos.

Estos cambios nos permiten ofrecer una solución más escalable y user-friendly. Para explicar cómo logramos esta arquitectura, le cedo la palabra a [Braulio]."

## 👤 Tello - Arquitectura y Proceso de Abstracción (60 segundos)
"Gracias Omar. Nuestra arquitectura ahora implementa claramente el patrón MVC:

• El Modelo incluye nuestras entidades Hibernate como 'INE' y 'Usuario'
• La Vista son nuestras interfaces HTML conectadas via HTTP
• Los Controladores son servlets que gestionan las peticiones

El Diagrama de Clases evolucionó significativamente desde la primera entrega. Ahora tenemos relaciones claras como composición entre 'Documento' y 'Campos', y herencia para diferentes tipos de validación. Cada clase corresponde directamente a requisitos funcionales específicos.

*Por ejemplo, la clase 'ValidadorCURP' implementa directamente el RF-03 de validación de CURP. [Cesar] les mostrará cómo funciona en la práctica."*

## 👤 Cesar - Demostración y Refinamiento de Requisitos (70 segundos)
"Perfecto. Ahora vamos a ver el sistema funcionando con los nuevos requisitos refinados. En esta interfaz web pueden observar cómo hemos implementado los criterios de aceptación definidos en nuestras historias de usuario.

*Al posicionar el documento... el sistema no solo captura el texto, sino que valida contra nuestra base de datos H2 - implementando el requisito no funcional RNF-02 de prevención de duplicados.*

Pueden ver en consola cómo se ejecuta el flujo completo: desde la vista HTML, pasando por el controlador Servlet, hasta el modelo Hibernate. 
 Evolución de Requisitos y Gestión del Proceso (60 segundos)
"Lo que distingue nuestra evolución es cómo refinamos los requisitos basados en feedback real. Actualizamos nuestro diagrama de casos de uso y priorizamos los RF usando el método MoSCoW:

• MUST: Validación en tiempo real (85% precisión)
• SHOULD: Interfaz web responsive
• COULD: Persistencia en base de datos

*Gestionamos este proceso mediante sprints de 2 semanas, con reuniones diarias documentadas en nuestro Discord. Las métricas de contribución individual muestran distribución equitativa: 25% frontend, 35% backend, 25% base de datos, 15% documentación - todo rastreable en GitHub."*

## 👤 Alonso - Competencias y Conclusiones Técnicas (50 segundos)
"Este proyecto fortaleció competencias genéricas como trabajo en equipo mediante herramientas como GitHub Projects, y competencias específicas de ingeniería de software mediante la implementación de MVC y Hibernate.


