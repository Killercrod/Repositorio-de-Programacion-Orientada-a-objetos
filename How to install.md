# Instalación de OpenCV en Python

Este documento explica cómo configurar e instalar **OpenCV** en **Python**, tanto en **Mac** como en **Windows**, utilizando **VSCode** como entorno de desarrollo.

---

## 📌 Requisitos previos

- Tener instalado **VSCode** con la extensión de Python.  
- Contar con **Python 3.8 o superior** instalado.  
- Tener acceso a la terminal (cmd, PowerShell o terminal de Mac).  

---

## 🍏 Instalación en Mac

### 1. Instalar Xcode Command Line Tools
``` bash
xcode-select --install
2. Instalar Homebrew
Si no lo tienes instalado:
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
3. Instalar Python
Aunque macOS ya trae una versión, es recomendable instalar una actualizada:
brew install python
4. Verificar pip
Generalmente se instala junto con Python. Verifica con:
pip3 --version
5. Instalar OpenCV
pip3 install opencv-python
pip3 install opencv-python-headless   # opcional, para entornos sin GUI
6. Verificar instalación
En VSCode crea un archivo test.py con el siguiente contenido:
import cv2
print(cv2.__version__)
Ejecuta:
python3 test.py
Si aparece la versión de OpenCV, la instalación fue exitosa ✅
🪟 Instalación en Windows
1. Instalar Python
Descarga la última versión desde la página oficial:
👉 https://www.python.org/downloads/
Durante la instalación marca la opción "Add Python to PATH".

Verifica la instalación:

python --version
pip --version
2. Crear un entorno virtual (opcional pero recomendado)
En la carpeta de tu proyecto:
python -m venv venv
Activar el entorno:
venv\Scripts\activate
3. Instalar OpenCV
pip install opencv-python
pip install opencv-python-headless   # opcional, para entornos sin GUI
4. Verificar instalación
En VSCode crea un archivo test.py con:
import cv2
print(cv2.__version__)
Ejecuta:
python test.py
Si aparece la versión, OpenCV quedó correctamente instalado 🎉
⚡ Notas adicionales
Si usas Anaconda, puedes instalar OpenCV con:
conda install -c conda-forge opencv
En caso de errores con permisos, puedes agregar --user al instalar con pip:
pip install opencv-python --user
