from stockfish import Stockfish

stockfish = Stockfish(path="stockfish/stockfish-windows-x86-64-avx2")
def getBestMoveForComp(elo_rating,fen_body):
    stockfish.set_elo_rating(elo_rating)
    stockfish.set_fen_position(fen_body)
    return stockfish.get_best_move()

