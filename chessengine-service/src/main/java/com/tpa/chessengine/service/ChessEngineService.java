package com.tpa.chessengine.service;

import com.chesstpa.Game;
import com.tpa.chessengine.model.Move;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class ChessEngineService {
    private final Game game;
    public String getPossiblesMoves(String boardState,String pieceCoordinates) {
        return game.getPossibleMovesForPosition(boardState, pieceCoordinates);
    }
}
