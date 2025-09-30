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
