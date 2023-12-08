from flask import Flask, request, jsonify
import py_eureka_client.eureka_client as eureka_client

rest_port = 8060
eureka_client.init(eureka_server="http://localhost:8761/eureka",
                   app_name="analysis-service",
                   instance_port=rest_port)
app = Flask(__name__)


@app.route('/api/analysis/hello', methods=['GET'])
def hello():
    return jsonify(message='Hello, World!')

if __name__ == '__main__':
    app.run(debug=True)