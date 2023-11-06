package com.tpa.chessengine.service;

import com.chesstpa.communication.ChessEngine;
import com.tpa.chessengine.dto.GameStatusRequest;
import com.tpa.chessengine.dto.GameStatusResponse;
import com.tpa.chessengine.dto.MoveRequest;
import com.tpa.chessengine.dto.MoveResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChessEngineService {
    private final ChessEngine chessEngine;
    public ResponseEntity<Set<MoveResponse>> getPossiblesMoves(MoveRequest request) {
        String[] moves = chessEngine.getPossibleMovesForPosition(request.getBoardState(), request.getPiecePosition(), request.getWhiteCastle(), request.getBlackCastle()).split("/");
        return ResponseEntity.status(HttpStatus.OK).body(convertMoves(moves));
    }
    public ResponseEntity<GameStatusResponse> getGameStatus(GameStatusRequest request) {
        String status = chessEngine.getGameStatus(request.getBoardState(), request.getWhiteCastle(), request.getBlackCastle(), request.getColor());
        return ResponseEntity.status(HttpStatus.OK).body(convertStatus(status));
    }
    private Set<MoveResponse> convertMoves(String[] moves) {
        return Arrays.stream(moves)
                .map(move -> {
                    int x = move.toUpperCase().charAt(0) - 'A';
                    int y = Integer.parseInt(move.substring(1).trim()) - 1;
                    return new MoveResponse(x, y);
                })
                .collect(Collectors.toSet());
    }
    private GameStatusResponse convertStatus(String status){
        if(Objects.equals(status, "Checkmate")){
            return GameStatusResponse.CHECKMATE;
        } else if (Objects.equals(status, "Pat")) {
            return GameStatusResponse.PAT;
        } else return GameStatusResponse.GAME;
    }
}
