from flask import Flask, jsonify
import pycode
import json
import os

app = Flask(__name__)

@app.route('/tomar-foto')
def tomar_foto():
    try:
        pycode.tomar_foto_y_guardar()

        if os.path.exists("####.json"):
            with open("#####.json", "r") as f:
                datos = json.load(f)
            return jsonify({
                "mensaje": "Foto capturada y procesada con éxito",
                "resultado": datos
            })
        else:
            return jsonify({"error": "No se encontró el archivo JSON"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5000)
