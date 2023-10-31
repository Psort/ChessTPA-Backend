package com.tpa.chessengine.controller;

import com.tpa.chessengine.dto.GameStatusRequest;
import com.tpa.chessengine.dto.GameStatusResponse;
import com.tpa.chessengine.dto.MoveRequest;
import com.tpa.chessengine.dto.MoveResponse;
import com.tpa.chessengine.service.ChessEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/engine")
@RequiredArgsConstructor
public class ChessEngineController {

    private final ChessEngineService chessEngineService;
    @PostMapping("/move")
    public ResponseEntity<Set<MoveResponse>> getPossibleMoves(@RequestBody MoveRequest request ){
        return chessEngineService.getPossiblesMoves(request);
    }
    @PostMapping("/status")
    public ResponseEntity<GameStatusResponse> getGameStatus(@RequestBody GameStatusRequest request ){
        return chessEngineService.getGameStatus(request);
    }
}
