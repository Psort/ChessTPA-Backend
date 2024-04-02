
from flask import Flask, request,json
import py_eureka_client.eureka_client as eureka_client

from stockfishService import getBestMoveForComp

rest_port = 8060
eureka_client.init(eureka_server="http://localhost:8761/eureka",
                   app_name="analysis-service",
                   instance_port=rest_port)
app = Flask(__name__)


@app.route('/api/analysis/computer', methods=['GET'])
def get_computer_move():
    elo_rating = request.args.get('eloRating')
    fen_body = request.args.get('fenBody')
    print(elo_rating, fen_body)
    return  getBestMoveForComp(elo_rating,fen_body)



if __name__ == '__main__':
    app.run(debug=True)
