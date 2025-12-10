## Requisitos

# Requisitos Funcionales (RF)

RF-001: Captura de video en tiempo real

Como usuario del sistema, quiero que la cámara capture video continuamente para procesar documentos en tiempo real
RF-002: Procesamiento de imagen para OCR

Como sistema, quiero aplicar filtros de escala de grises, desenfoque y umbralización adaptativa para optimizar el reconocimiento de texto
RF-003: Reconocimiento de patrones de INE

Como sistema, quiero identificar campos específicos (NOMBRE Y CURP) para validar autenticidad del documento
RF-004: Interfaz de usuario visual

Como usuario, quiero ver indicadores gráficos de estado y resultados para entender el proceso de verificación

# Requisitos No Funcionales (RNF)

RNF-001: Validacion de ID

El sistema debe verificar que la imagen corresponda a una identificación oificiks 
válida 
El sistema debe rechazar imágenes que no contengan un documento válido 
Captura de imágenes
El sistema debe permitir la toma de fotografías de la identificación oficial mediante 
cámara integrada 

RNF-002: Reconocimiento de texto (OCR)

El sistema debe limitar el reconocimiento de texto únicamente al idioma español
El sistema debe procesar la imagen de la identificación para extraer el texto 
mediante el OCR.
El sistema debe garantizar que el texto extraído se guarde en un formato 
estructurado (por ejemplo, JSON)

RNF-003:Detección de palabras clave

El sistema debe identificar y extraer campos relevantes de la identificación, como 
Nombre, CURP, Fecha de nacimiento, etc.
El sistema debe validar que los campos detectados correspondan a un formato 
correcto (por ejemplo, que la CURP tenga 18 caracteres válidos, que la fecha esté 
en formato día,mes,año.

RNF-004: Almacenamiento de información

El sistema debe guardar tanto las imágenes originales como el texto 
reconocido en un repositorio local

Priorización

Método: MoSCoW

Must have: RF-001, RF-002, RF-003 (funcionalidad base)
Should have: RF-004 (flexibilidad de almacenamiento local)
Could have: Reconocimiento de múltiples tipos de documentos
Won't have: Almacenamiento de datos personales
