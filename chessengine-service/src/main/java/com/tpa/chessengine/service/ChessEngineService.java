package com.tpa.chessengine.service;

import com.chesstpa.Game;
import com.tpa.chessengine.dto.MoveRequest;
import com.tpa.chessengine.dto.MoveResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChessEngineService {
    private final Game game;
    public ResponseEntity<Set<MoveResponse>> getPossiblesMoves(MoveRequest request) {
        String[] moves = game.getPossibleMovesForPosition(request.getBoardState(), request.getPiecePosition()).split("/");
        return ResponseEntity.status(HttpStatus.OK).body(convertMoves(moves));
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
}
