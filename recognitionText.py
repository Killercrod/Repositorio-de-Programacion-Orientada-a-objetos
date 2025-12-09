import cv2
import pytesseract
import re
import os
#os.environ['TESSDATA_PREFIX'] = '/opt/homebrew/share/tessdata'
os.environ['TESSDATA_PREFIX'] = r'C:\Program Files\Tesseract-OCR\tessdata'
#Variables
cuadro = 100
doc = 0


cap = cv2.VideoCapture(0)
cap.set(3,1280)
cap.set(4,740)

def texto(imagen):
    global doc
    
    
    # Dirección Pytesseract
    #pytesseract.pytesseract.tesseract_cmd = '/opt/homebrew/bin/tesseract'
    pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
  
    #Escala de grises
    gris = cv2.cvtColor(imagen, cv2.COLOR_BGR2GRAY)
    # Filtro de ruido
    gris = cv2.medianBlur(gris, 3)
    #Filtro
    umbral= cv2.adaptiveThreshold(gris,255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 31, 15)
    
    #Configuracion OCR
    config = ' --psm 6 -l spa'
    texto = pytesseract.image_to_string(umbral,config=config)
    # Limpieza del texto
    texto = re.sub(r'[^A-Z0-9\s]', '', texto.upper())
    #Palabras Clave Ine mas reciente
    nombre = r'NOMBRE'
    domicilio = r'DOMICILIO'
    curp = r'CURP'
    fechaDeNacimiento = r'FECHA DE NACIMIENTO'
    
    busqueda1 = re.findall(nombre, texto)
    busqueda2 = re.findall(domicilio, texto)
    busqueda3 = re.findall(curp, texto)
    busqueda4 = re.findall(fechaDeNacimiento, texto)
    if len(busqueda1) != 0 and len(busqueda2) != 0 and len(busqueda3) != 0 and len(busqueda4)!= 0:
        doc = 1
    print(texto)

#Empezar 
while True:
    #Lectura de Videocaptura
    ret, frame = cap.read()
    
    #interfaz
    cv2.putText(frame,'Ubique el documento a identificar',(450,80 - cuadro),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX, 0.71,(0,255,0),2)
    cv2.rectangle(frame,(cuadro,cuadro),(1280 - cuadro, 720 - cuadro), (0,255,0), 2)
    
    #Opciones
    if doc == 0:
        cv2.putText(frame,'Presiona S para identificar',(470,750 - cuadro),cv2.FONT_HERSHEY_SCRIPT_SIMPLEX, 0.71,(0,255,0),2)
    elif doc ==  1:
        cv2.putText(frame ,'INE', (470, 750 - cuadro),cv2.FONT_HERSHEY_SIMPLEX, 0.71,(0,255,255),2) 
        print('Ine:')
        
    #Leemos nuestro teclado
    t = cv2.waitKey(5)
    
    cv2.imshow('ID INTELIGENTE',frame)
    
    #Escape
    if t == 27:
        break
    elif t == 83 or t == 115:
        texto(frame)
        
cap.release()
cv2.destroyAllWindows()