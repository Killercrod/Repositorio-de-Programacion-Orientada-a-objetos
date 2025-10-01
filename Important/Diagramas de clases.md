![Imagen de WhatsApp 2025-09-30 a las 22 41 30_4c9cde00](https://github.com/user-attachments/assets/9bcef142-4614-4292-b650-a847671865fa)

### En Terminos Textuales: 
class SistemaReconocimiento {
  - capturadora: VideoCapture
  - procesadorOCR: OCRProcessor
  - validador: DocumentValidator
  + iniciarCaptura()
  + procesarFrame()
  + validarDocumento()
}

class OCRProcessor {
  - configTesseract: string
  + preprocesarImagen()
  + extraerTexto()
  + limpiarTexto()
}

class DocumentValidator {
  - patrones: string[]
  + buscarPatrones()
  + validarINE()
}
