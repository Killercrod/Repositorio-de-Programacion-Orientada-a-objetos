import cv2
import pytesseract
import re
import os
import numpy as np
import argparse
import json

#os.environ['TESSDATA_PREFIX'] = '/opt/homebrew/share/tessdata' #Funciona para la lap de Cesar
os.environ['TESSDATA_PREFIX'] = r'C:\Program Files\Tesseract-OCR\tessdata' #Funciona para mi lap (Tello)
#Configuracion del parser
parser = argparse.ArgumentParser(
    description="Sistema de reconocimiento de INE con opción de fuente de imagen."
)
# Argumento opcional para fuente de imagen
parser.add_argument(
    "--img-source",
    type=str,
    help="Ruta de archivo o URL de la imagen a procesar en lugar de usar la cámara"
)

args = parser.parse_args()
# Variables
cuadro = 100
doc = 0

# Crear carpeta para guardar los pasos si no existe
if not os.path.exists("pasos_procesamiento"):
    os.makedirs("pasos_procesamiento")

def extraer_datos_estructurados(texto_completo, curps_encontradas):
    """Extrae datos estructurados del texto del INE"""
    
    datos = {
        "documento_identificado": False,
        "tipo_documento": "INE",
        "datos_personales": {},
        "confianza": "media"
    }
    
    # Buscar CURP
    if curps_encontradas:
        datos["datos_personales"]["curp"] = curps_encontradas[0]
        datos["documento_identificado"] = True
        return datos
def mejorar_contraste(imagen):
    """Mejora el contraste usando CLAHE"""
    lab = cv2.cvtColor(imagen, cv2.COLOR_BGR2LAB)
    lightness, a_channel, b_channel = cv2.split(lab)
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
    lightness_mejorado = clahe.apply(lightness)
    lab_mejorado = cv2.merge([lightness_mejorado, a_channel, b_channel])
    return cv2.cvtColor(lab_mejorado, cv2.COLOR_LAB2BGR)

def eliminar_sombra(imagen):
    """Elimina sombras usando filtrado homomórfico"""
    # Convertir a float32 para procesamiento
    imagen_float = np.float32(imagen) / 255.0
    
    # Aplicar logaritmo para separar iluminación y reflectancia
    log_imagen = np.log(imagen_float + 0.01)
    
    # Filtro homomórfico en el dominio de la frecuencia
    rows, cols = imagen.shape[:2]
    crow, ccol = rows // 2, cols // 2
    D0, gammaH, gammaL = 30, 2.0, 0.5
    
    # Crear filtro butterworth
    u, v = np.meshgrid(np.arange(cols), np.arange(rows))
    D = np.sqrt((u - ccol)**2 + (v - crow)**2)
    H = (gammaH - gammaL) * (1 - np.exp(-(D**2) / (2 * D0**2))) + gammaL
    
    # Aplicar filtro a cada canal
    resultado = np.zeros_like(log_imagen)
    for i in range(3):
        f = np.fft.fft2(log_imagen[:,:,i])
        fshift = np.fft.fftshift(f)
        fshift_filtered = fshift * H
        f_filtered = np.fft.ifftshift(fshift_filtered)
        resultado[:,:,i] = np.real(np.fft.ifft2(f_filtered))
    
    # Exponenciar y normalizar
    resultado = np.exp(resultado) - 0.01
    return np.uint8(np.clip(resultado * 255, 0, 255))

def preprocesamiento_avanzado(imagen, contador_procesamiento=0):
    """Pipeline completo de preprocesamiento para INE"""
    
    # Guardar imagen original
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_original.jpg", imagen)
    
    # Paso 1: Eliminar sombras
    sin_sombra = eliminar_sombra(imagen)
    cv2.imshow("1. Sin sombra", sin_sombra)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_01_sin_sombra.jpg", sin_sombra)
    cv2.waitKey(300)
    
    # Paso 2: Mejorar contraste
    alto_contraste = mejorar_contraste(sin_sombra)
    cv2.imshow("2. Alto contraste", alto_contraste)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_02_alto_contraste.jpg", alto_contraste)
    cv2.waitKey(300)
    
    # Paso 3: Convertir a escala de grises
    gris = cv2.cvtColor(alto_contraste, cv2.COLOR_BGR2GRAY)
    cv2.imshow("3. Grises", gris)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_03_grises.jpg", gris)
    cv2.waitKey(300)
    
    # Paso 4: Filtro bilateral para reducir ruido preservando bordes
    gris_suavizado = cv2.bilateralFilter(gris, 5, 50, 50)
    cv2.imshow("4. Bilateral Filter", gris_suavizado)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_04_bilateral_filter.jpg", gris_suavizado)
    cv2.waitKey(300)
    
    # Paso 5: Ecualización local del histograma
    clahe = cv2.createCLAHE(clipLimit=1.0, tileGridSize=(8, 8))
    gris_ecualizado = clahe.apply(gris_suavizado)
    cv2.imshow("5. CLAHE", gris_ecualizado)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_05_clahe.jpg", gris_ecualizado)
    cv2.waitKey(300)
    
    # Paso 6: Umbral adaptativo con parámetros optimizados
    umbral = cv2.adaptiveThreshold(
        gris_ecualizado, 255, 
        cv2.ADAPTIVE_THRESH_GAUSSIAN_C, 
        cv2.THRESH_BINARY, 41, 15
        
   
       
    )
    cv2.imshow("6. Umbral Adaptativo", umbral)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_06_umbral_adaptativo.jpg", umbral)
    cv2.waitKey(300)
    
    
    # Paso 7: EROSIÓN MORFOLÓGICA (Separación agresiva)
    umbral_invertido = cv2.bitwise_not(umbral)
    kernel = np.ones((1, 1), np.uint8)
    erosion = cv2.erode(umbral_invertido, kernel, iterations=2)
    cv2.imshow("7. Erosión Morfológica", erosion)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_07_erosion.jpg", erosion)
    cv2.waitKey(300)
    
    
    # Paso 8: OPENING MORFOLÓGICO (Reparación y limpieza)
    opening = cv2.morphologyEx(erosion, cv2.MORPH_OPEN, kernel)
    cv2.imshow("8. Opening Morfológico", opening)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_08_opening.jpg", opening)
    cv2.waitKey(300)
    
    return opening

def mejorar_reconocimiento_texto(imagen, contador_procesamiento=0):
    """OCR mejorado con múltiples configuraciones"""
    
    # Preprocesamiento avanzado
    imagen_procesada = preprocesamiento_avanzado(imagen, contador_procesamiento)
    
    # Guardar resultado final del preprocesamiento
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_08_resultado_final.jpg", imagen_procesada)
    
    # Configuraciones optimizadas de OCR
    configuraciones = [
        '--psm 6 -c tessedit_char_whitelist=ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ÁÉÍÓÚÑ./- --oem 3 -l spa',
        '--psm 4 -c tessedit_char_whitelist=ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ÁÉÍÓÚÑ./- --oem 3 -l spa'
    ]
    
    textos = []
    
    for config in configuraciones:
        try:
            texto = pytesseract.image_to_string(imagen_procesada, config=config)
            if texto.strip():
                textos.append(texto)
        except Exception as e:
            print(f"Error en OCR: {e}")
    
    # Combinar resultados
    texto_combinado = " ".join(textos)
    return texto_combinado

# Contador para organizar los archivos por procesamiento
contador_global = 0

def texto(imagen):
    global doc, contador_global
    doc= 0
    # Dirección Pytesseract
    pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe' #/opt/homebrew/share/tessdata cambio para que funcione en mi lap

    # Incrementar contador para nuevo procesamiento
    contador_global += 1
    print(f"Iniciando procesamiento {contador_global}")

    # OCR mejorado que incluye todos los pasos de preprocesamiento
    texto_extraido = mejorar_reconocimiento_texto(imagen, contador_global)

    # Limpieza del texto
    texto = re.sub(r'[^A-Z0-9\s]', '', texto_extraido.upper())

    patron_curp = r'[A-Z]{4}[0-9]{6}[HM]{1}[A-Z]{5}[A-Z0-9]{1}[0-9]{1}'
    busqueda1 = re.findall(patron_curp, texto)

    # Misma verificación original pero solo con nombre y curp
    if  len(busqueda1) != 0:
        doc = 1

    print("="*50)
    print(f"PROCESAMIENTO {contador_global}")
    print("="*50)
    print("Texto detectado:", texto)
    print(f"Coincidencias -  CURP: {len(busqueda1)}")
    print(f"Documento identificado: {'SI' if doc == 1 else 'NO'}")
    print("="*50)
    
    # Generar JSON básico con CURP
    datos_json = {
        "documento_identificado": doc == 1,
        "tipo_documento": "INE",
        "curp_detectada": busqueda1[0] if busqueda1 else None,
        "total_coincidencias_curp": len(busqueda1),
        "texto_extraido": texto[:200]  # Primeros 200 caracteres para referencia
    }
    
    print("DATOS CURP EN JSON:")
    print(json.dumps(datos_json, indent=2, ensure_ascii=False))
    print("="*50)
    
    return datos_json
#Para que no ejecute el el codigo de camara en loop y solo se ejecute cuando pycode este en tipo script, no al importarlo
def tomar_foto_y_guardar():
    # función que contiene el flujo de captura/procesamiento y crea el JSON
    pass

# Modo imagen estática vs modo cámara
if args.img_source:
    print(f"Procesando imagen: {args.img_source}")
    
    # Cargar imagen desde archivo
    imagen = cv2.imread(args.img_source)
    if imagen is None:
        print(f"Error: No se pudo cargar la imagen {args.img_source}")
        exit()
    
    # Procesar la imagen
    texto(imagen)
      # Guardar JSON en archivo
    nombre_archivo = os.path.splitext(os.path.basename(args.img_source))[0]
    ruta_json = f"pasos_procesamiento/{nombre_archivo}_curp.json"
    
    with open(ruta_json, 'w', encoding='utf-8') as f:
        json.dump(texto(imagen), f, indent=2, ensure_ascii=False)
    
    print(f"✅ Datos CURP guardados en: {ruta_json}")
  
    # Esperar a que el usuario presione una tecla para cerrar las ventanas
    print("Presiona cualquier tecla en una de las ventanas de imagen para cerrar...")
    cv2.waitKey(0)
    cv2.destroyAllWindows()
    exit()  # Salir después de procesar la imagen

else:
    # Modo cámara
    cap = cv2.VideoCapture(0)
    cap.set(3, 1280)
    cap.set(4, 740)

# Empezar 
while True:
    # Lectura de Videocaptura
    ret, frame = cap.read()
    
    # Interfaz
    cv2.putText(frame,'Ubique el documento a identificar',(450,80 - cuadro),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX, 0.71,(0,255,0),2)
    
    if not ret or frame is None:
        print("Error: No se pudo capturar el frame. ¿Cámara conectada?")
        break
    # Coordenadas para centrar el cuadro
    center_x = frame.shape[1] // 2
    center_y = frame.shape[0] // 2
    width = 600
    height = 400

    top_left = (center_x - width // 2, center_y - height // 2)
    bottom_right = (center_x + width // 2, center_y + height // 2)

    cv2.rectangle(frame, top_left, bottom_right, (0,255,0), 2)
    
    # Opciones
    if doc == 0:
        cv2.putText(frame,'Presiona S para identificar',(470,750 - cuadro),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX, 0.71,(0,255,0),2)
    elif doc ==  1:
        cv2.putText(frame ,'INE', (470, 750 - cuadro),cv2.FONT_HERSHEY_SIMPLEX, 0.71,(0,255,255),2) 
        print('Ine:')
        
    # Leemos nuestro teclado
    t = cv2.waitKey(5)
    
    cv2.imshow('ID INTELIGENTE',frame)
    
    # Escape
    if t == 27:
        break
    elif t == 83 or t == 115:
        # Solo captura lo que está dentro del cuadro
        roi = frame[top_left[1]:bottom_right[1], top_left[0]:bottom_right[0]]
        texto(roi)
        
cap.release()
cv2.destroyAllWindows()
#Para que no ejecute el el codigo de camara en loop y solo se ejecute cuando pycode este en tipo script, no al importarlo
if __name__ == "__main__":
    # solo al ejecutar `python pycode.py` se hace la captura directa
    tomar_foto_y_guardar()
