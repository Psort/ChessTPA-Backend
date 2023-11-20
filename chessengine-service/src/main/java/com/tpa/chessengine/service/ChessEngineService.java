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
    public Set<MoveResponse> getPossiblesMoves(MoveRequest request) {
        String castles = convertCastlesToString(request.getCastles());
        String[] moves = chessEngine.getPossibleMovesForPosition(request.getBoardState(), request.getPiecePosition(), castles).split("/");
        return convertMoves(moves);
    }
    public GameStatusResponse getGameStatus(GameStatusRequest request) {
        String castles = convertCastlesToString(request.getCastles());
        String status = chessEngine.getGameStatus(request.getBoardState(), castles, request.getColor());
        return convertStatus(status);
    }
    private Set<MoveResponse> convertMoves(String[] moves) {
        if(Arrays.toString(moves).equals("[]")){
            return null;
        }
        return Arrays.stream(moves)
                .map(move -> {
                    int x = Integer.parseInt(move.substring(1).trim());
                    int y = move.toUpperCase().charAt(0) - 'A';
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
    private String convertCastlesToString(String[] castles){
        return String.join("", castles);
    }
}
