import cv2
import pytesseract
import re
import os
import numpy as np

os.environ['TESSDATA_PREFIX'] = '/opt/homebrew/share/tessdata'

# Variables
cuadro = 100
doc = 0

# Crear carpeta para guardar los pasos si no existe
if not os.path.exists("pasos_procesamiento"):
    os.makedirs("pasos_procesamiento")

cap = cv2.VideoCapture(0)
cap.set(3, 1280)
cap.set(4, 740)

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
    gris_suavizado = cv2.bilateralFilter(gris, 9, 75, 75)
    cv2.imshow("4. Bilateral Filter", gris_suavizado)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_04_bilateral_filter.jpg", gris_suavizado)
    cv2.waitKey(300)
    
    # Paso 5: Ecualización local del histograma
    clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8, 8))
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
    return umbral

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

    # Dirección Pytesseract
    pytesseract.pytesseract.tesseract_cmd = '/opt/homebrew/bin/tesseract'

    # Incrementar contador para nuevo procesamiento
    contador_global += 1
    print(f"Iniciando procesamiento {contador_global}")

    # OCR mejorado que incluye todos los pasos de preprocesamiento
    texto_extraido = mejorar_reconocimiento_texto(imagen, contador_global)

    # Limpieza del texto
    texto = re.sub(r'[^A-Z0-9\s]', '', texto_extraido.upper())
    nombre = r'NOMBRE'
    domicilio = r'DOMICILIO'
    curp = r'CURP'
    fechaDeNacimiento = r'FECHA DE NACIMIENTO'

    busqueda1 = re.findall(nombre, texto)
    busqueda2 = re.findall(domicilio, texto)
    busqueda3 = re.findall(curp, texto)
    busqueda4 = re.findall(fechaDeNacimiento, texto)
    
    # Misma verificación original
    if len(busqueda1) != 0 and len(busqueda2) != 0 and len(busqueda3) != 0 and len(busqueda4) != 0:
        doc = 1
    
    print("="*50)
    print(f"PROCESAMIENTO {contador_global}")
    print("="*50)
    print("Texto detectado:", texto)
    print(f"Coincidencias - NOMBRE: {len(busqueda1)}, DOMICILIO: {len(busqueda2)}, CURP: {len(busqueda3)}, FECHA: {len(busqueda4)}")
    print(f"Documento identificado: {'SI' if doc == 1 else 'NO'}")
    print("="*50)

# Empezar 
while True:
    # Lectura de Videocaptura
    ret, frame = cap.read()
    
    # Interfaz
    cv2.putText(frame,'Ubique el documento a identificar',(450,80 - cuadro),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX, 0.71,(0,255,0),2)
    
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
#Que me hace falta 
#Me hace falta que detecte el contorno del documento y me lo indique por ejemplo hasta que la identificacion este en el cuadro se ponga verde el las lineas del cuadro (opcional pero util)
#Apartir de que tome la foto que procese individualmente cada cuadro tomado debido a medidas ya especificas
#Convertir esos datos en variables para despues guardarlos en una base de datos
#Volverlo una api