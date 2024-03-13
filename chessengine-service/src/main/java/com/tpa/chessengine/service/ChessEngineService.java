package com.tpa.chessengine.service;

import com.chesstpa.board.Position;
import com.chesstpa.communication.ChessEngine;
import com.tpa.chessengine.dto.GameStatusRequest;
import com.tpa.chessengine.dto.GameStatusResponse;
import com.tpa.chessengine.dto.MoveRequest;
import com.tpa.chessengine.dto.MoveResponse;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChessEngineService {
    private final ChessEngine chessEngine;
    public Set<MoveResponse> getPossiblesMoves(MoveRequest request) {
        String castles = convertCastlesToString(request.getCastles());
        Map<Position, List<Position>> allPossiblePosition = chessEngine.getAllPossibleMovesForColor(request.getBoardState(),request.getPlayerColor().name(),castles);
        return  allPossiblePosition.entrySet().stream()
                .map(entry -> new MoveResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }
    public GameStatusResponse getGameStatus(GameStatusRequest request) {
        String castles = convertCastlesToString(request.getCastles());
        String status = chessEngine.getGameStatus(request.getBoardState(), castles, request.getColor());
        return convertStatus(status);
    }
    private GameStatusResponse convertStatus(String status){
        return Objects.equals(status, "Checkmate") ? GameStatusResponse.CHECKMATE :
                Objects.equals(status, "Pat") ? GameStatusResponse.PAT :
                        GameStatusResponse.GAME;

    }
    private String convertCastlesToString(String[] castles){
        return String.join("", castles);
    }
}
