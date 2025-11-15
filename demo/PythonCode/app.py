from flask import Flask, jsonify
import os
import pycode

app = Flask(__name__)

@app.route('/tomar-foto')
def tomar_foto():
    try:
        datos = pycode.tomar_foto_y_guardar()
        if datos is None:
            return jsonify({"error": "No se pudo capturar la imagen"}), 500

        # devolver directamente el JSON resultante
        return jsonify({
            "mensaje": "Foto capturada y procesada con éxito",
            "resultado": datos
        }), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5000)
