import cv2
import pytesseract
import re
import os
import numpy as np
import argparse
import json

os.environ['TESSDATA_PREFIX'] = '/opt/homebrew/share/tessdata'

# Variables
cuadro = 100
doc = 0

# Crear carpeta para guardar los pasos si no existe
if not os.path.exists("pasos_procesamiento"):
    os.makedirs("pasos_procesamiento")

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
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_01_sin_sombra.jpg", sin_sombra)
    
    # Paso 2: Mejorar contraste
    alto_contraste = mejorar_contraste(sin_sombra)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_02_alto_contraste.jpg", alto_contraste)
    
    # Paso 3: Convertir a escala de grises
    gris = cv2.cvtColor(alto_contraste, cv2.COLOR_BGR2GRAY)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_03_grises.jpg", gris)
    
    # Paso 4: Filtro bilateral para reducir ruido preservando bordes
    gris_suavizado = cv2.bilateralFilter(gris, 5, 50, 50)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_04_bilateral_filter.jpg", gris_suavizado)
    
    # Paso 5: Ecualización local del histograma
    clahe = cv2.createCLAHE(clipLimit=1.0, tileGridSize=(8, 8))
    gris_ecualizado = clahe.apply(gris_suavizado)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_05_clahe.jpg", gris_ecualizado)
    
    # Paso 6: Umbral adaptativo con parámetros optimizados
    umbral = cv2.adaptiveThreshold(
        gris_ecualizado, 255, 
        cv2.ADAPTIVE_THRESH_GAUSSIAN_C, 
        cv2.THRESH_BINARY, 41, 15
    )
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_06_umbral_adaptativo.jpg", umbral)
    
    # Paso 7: EROSIÓN MORFOLÓGICA (Separación agresiva)
    umbral_invertido = cv2.bitwise_not(umbral)
    kernel = np.ones((1, 1), np.uint8)
    erosion = cv2.erode(umbral_invertido, kernel, iterations=2)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_07_erosion.jpg", erosion)
    
    # Paso 8: OPENING MORFOLÓGICO (Reparación y limpieza)
    opening = cv2.morphologyEx(erosion, cv2.MORPH_OPEN, kernel)
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_08_opening.jpg", opening)
    
    return opening

def mejorar_reconocimiento_texto(imagen, contador_procesamiento=0):
    """OCR mejorado con múltiples configuraciones"""
    
    # Preprocesamiento avanzado
    imagen_procesada = preprocesamiento_avanzado(imagen, contador_procesamiento)
    
    # Guardar resultado final del preprocesamiento
    cv2.imwrite(f"pasos_procesamiento/{contador_procesamiento:02d}_09_resultado_final.jpg", imagen_procesada)
    
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

def procesar_rois(roi_nombre, roi_curp):
    """Procesa ambos ROIs (nombre y CURP) por separado y extrae datos"""
    global contador_global
    doc = 0
    # Dirección Pytesseract
    pytesseract.pytesseract.tesseract_cmd = '/opt/homebrew/bin/tesseract'

    # Incrementar contador para nuevo procesamiento
    contador_global += 1
    print(f"Iniciando procesamiento {contador_global}")

    # Procesar cada ROI individualmente
    texto_nombre = mejorar_reconocimiento_texto(roi_nombre, contador_global*10)
    texto_curp = mejorar_reconocimiento_texto(roi_curp, contador_global*10 + 1)
    
    # Limpiar textos
    texto_nombre_limpio = re.sub(r'\s+', ' ', texto_nombre.strip())
    texto_curp_limpio = re.sub(r'\s+', ' ', texto_curp.strip())
    
    # Buscar CURP
    patron_curp = r'[A-Z]{4}[0-9]{6}[HMX]{1}[A-Z]{5}[A-Z0-9]{1}[0-9]{1}'
    curp_encontrada = re.findall(patron_curp, texto_curp_limpio.upper())
    
    # Determinar si se identificó documento
    documento_identificado = len(curp_encontrada) > 0
    
    # Generar JSON completo
    datos_json = {
        "documento_identificado": documento_identificado,
        "curp_encontrada": curp_encontrada[0] if curp_encontrada else "",
        "texto_nombre": texto_nombre_limpio[:200],
        "texto_curp": texto_curp_limpio[:200],
        "procesamiento_exitoso": True,
        "contador_procesamiento": contador_global
    }
    
    print("="*50)
    print(f"PROCESAMIENTO {contador_global}")
    print("="*50)
    print("Texto NOMBRE detectado:", texto_nombre_limpio)
    print("Texto CURP detectado:", texto_curp_limpio)
    print(f"Coincidencias - CURP: {len(curp_encontrada)}")
    print(f"Documento identificado: {'SI' if documento_identificado else 'NO'}")
    print("="*50)
    
    return datos_json

def procesar_imagen_completa(ruta_imagen):
    """Procesa una imagen completa de INE y extrae datos"""
    # Cargar imagen
    imagen = cv2.imread(ruta_imagen)
    if imagen is None:
        print(f"Error: No se pudo cargar la imagen {ruta_imagen}")
        return {"error": "No se pudo cargar la imagen", "procesamiento_exitoso": False}
    
    # Redimensionar si es necesario para estandarizar (mismo tamaño que en modo cámara)
    alto, ancho = imagen.shape[:2]
    if ancho != 505 or alto != 319:
        imagen = cv2.resize(imagen, (505, 319))
        print(f"Imagen redimensionada a 505x319 (original: {ancho}x{alto})")
#     Verde:Nombre
# * 		top: 75px;
# * 		
# * 		    left: 148px;
# * 		
# * 		    width: 132px;
# * 		
# * 		    height: 79px;
# Azul:Curp
# * 		top: 252px;
# * 		
# * 		    left: 148px;
# * 		
# * 		    width: 150px;
# * 		
# * 		    height: 24px;
    # Definir ROIs (regiones de interés) - mismas coordenadas que en modo cámara
    # ROI para NOMBRE (Verde)
    roi_nombre = imagen[75:75+79, 148:148+132]
    
    # ROI para CURP (Azul)
    roi_curp = imagen[252:252+24, 148:148+150]
    
    # Procesar los ROIs
    return procesar_rois(roi_nombre, roi_curp)

# Esto solo se ejecuta si el archivo se ejecuta directamente, no cuando se importa
if __name__ == '__main__':
    # Configuración del parser - SOLO cuando se ejecuta directamente
    parser = argparse.ArgumentParser(
        description="Sistema de reconocimiento de INE con opción de fuente de imagen."
    )
    # Argumento obligatorio para fuente de imagen
    parser.add_argument(
        "--img-source",
        type=str,
        required=True,
        help="Ruta de archivo de la imagen a procesar"
    )

    args = parser.parse_args()
    
    # Solo procesamos la imagen proporcionada como argumento
    if args.img_source:
        print(f"Procesando imagen: {args.img_source}")
        
        datos = procesar_imagen_completa(args.img_source)
        
        # Guardar JSON en archivo
        nombre_archivo = os.path.splitext(os.path.basename(args.img_source))[0]
        ruta_json = f"pasos_procesamiento/{nombre_archivo}_procesado.json"
        
        with open(ruta_json, 'w', encoding='utf-8') as f:
            json.dump(datos, f, indent=2, ensure_ascii=False)
        
        print(f"Datos guardados en: {ruta_json}")
        
        # Imprimir JSON para que Flask lo capture (última línea de salida)
        print(json.dumps(datos, ensure_ascii=False))
        
    else:
        print("Error: Se requiere --img-source con la ruta de la imagen")
        exit(1)