from flask import Flask, request, jsonify
import os
import pycode
from datetime import datetime

app = Flask(__name__)

@app.route('/procesar-ine', methods=['POST'])
def procesar_ine():
    try:
        if 'foto' not in request.files:
            return jsonify({"estado": "ERROR", "error": "No se recibió foto"})
        
        file = request.files['foto']
        if file.filename == '':
            return jsonify({"estado": "ERROR", "error": "No se seleccionó archivo"})
        
        # Guardar imagen temporal
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        temp_path = f"temp_ine_{timestamp}.jpg"
        file.save(temp_path)
        
        # Procesar con OCR usando tu código pycode
        datos = pycode.procesar_imagen_completa(temp_path)
        
        # Limpiar archivo temporal
        if os.path.exists(temp_path):
            os.remove(temp_path)
        
        return jsonify({
            "estado": "EXITO",
            "datos_extraidos": datos,
            "mensaje": "INE procesada correctamente"
        })
        
    except Exception as e:
        # Limpiar archivo temporal en caso de error
        if 'temp_path' in locals() and os.path.exists(temp_path):
            os.remove(temp_path)
        return jsonify({"estado": "ERROR", "error": str(e)})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)