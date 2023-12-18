package com.tpa.chessengine.controller;

import com.tpa.chessengine.dto.GameStatusRequest;
import com.tpa.chessengine.dto.GameStatusResponse;
import com.tpa.chessengine.dto.MoveRequest;
import com.tpa.chessengine.dto.MoveResponse;
import com.tpa.chessengine.service.ChessEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/engine")
@RequiredArgsConstructor
public class ChessEngineController {

    private final ChessEngineService chessEngineService;
    @PostMapping("/move")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Set<MoveResponse>> getPossibleMoves(@RequestBody MoveRequest request ){
        Set<MoveResponse> moveResponses = chessEngineService.getPossiblesMoves(request);
        return ResponseEntity.ok(moveResponses);
    }
    @PostMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GameStatusResponse> getGameStatus(@RequestBody GameStatusRequest request ){
        GameStatusResponse gameStatusResponse = chessEngineService.getGameStatus(request);
        System.out.print("2:");
        System.out.println(gameStatusResponse);
        return ResponseEntity.ok(gameStatusResponse);
    }
}
