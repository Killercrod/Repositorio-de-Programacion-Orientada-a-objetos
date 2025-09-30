👤 Omar - Introducción y Contexto (50 segundos)
Buenos días a todos. Hoy tenemos el gusto de presentarles un sistema innovador para la automatización de procesos de identificación.
En muchos entornos como instituciones financieras, servicios gubernamentales o control de acceso, existe una necesidad constante de verificar documentos de identidad de manera rápida y confiable.
Nuestro equipo ha desarrollado una solución que utiliza visión por computadora para identificar y validar INEs mexicanas en tiempo real, combinando técnicas avanzadas de procesamiento de imágenes con inteligencia artificial.
Para explicarles cómo funciona técnicamente, le cedo la palabra a [Braulio].

👤 Tello - Arquitectura Técnica (60 segundos)
El sistema se compone de tres módulos principales que trabajan en conjunto.
Primero, el módulo de captura utiliza OpenCV para obtener video en tiempo real. Luego se aplican una serie de filtros: conversión a escala de grises, desenfoque para reducir ruido, y umbralización adaptativa para mejorar el contraste.
El corazón del sistema es el motor OCR de Tesseract, específicamente configurado para reconocer texto en español. 
Finalmente, el validador busca patrones específicos como NOMBRE, DOMICILIO, CURP y FECHA DE NACIMIENTO.
Para mostrarles el sistema en acción, Cesar realizará una demostración.

👤 Cesar - Demostración en Vivo (70 segundos)
Perfecto. Ahora vamos a ver el sistema funcionando. Aquí tenemos la interfaz activa capturando video en tiempo real.
Pueden observar el rectángulo verde que delimita el área de captura óptima. Voy a posicionar el documento dentro de este marco...
Al presionar la tecla S, el sistema procesa el frame actual. Aplicamos inmediatamente los filtros de preprocesamiento que mencionó [Braulio].
En la consola podemos ver el texto extraído después de la limpieza... y ahora... ¡confirmación! El sistema ha identificado correctamente los cuatro campos requeridos.
Esta validación múltiple nos da una alta confiabilidad en el reconocimiento. [Persona 4] les explicará qué hace especial a nuestra solución.

👤 Raul - Propuesta de Valor e Innovación (60 segundos)
Lo que distingue a nuestro sistema son cuatro innovaciones clave:
Uno: Procesamiento en tiempo real que elimina la necesidad de escaneos estáticos. 
Dos: Configuración especializada para documentos mexicanos, no es una solución genérica.
Tres: Accesibilidad - funciona con cualquier cámara web estándar. 
Y cuatro: Algoritmos adaptativos que se ajustan a diferentes condiciones de iluminación.
Hemos logrado una precisión del 85% en condiciones controladas, con tiempos de procesamiento menores a 100 milisegundos por frame.
Para finalizar, [Alonso] compartirá los aprendizajes más valiosos de este proyecto.

👤 Alonso - Competencias y Conclusiones (50 segundos)
Este proyecto fue mucho más que desarrollar software; fue una experiencia integral de aprendizaje.
Desarrollamos competencias genéricas como trabajo en equipo efectivo y comunicación técnica, pero también competencias específicas de nuestra carrera: ingeniería de software, procesamiento digital de imágenes y sistemas inteligentes.
Hemos demostrado que es posible crear soluciones accesibles usando tecnologías de código abierto, llevando la teoría del aula a aplicaciones del mundo real.
En nombre de todo el equipo, agradezco su atención.
