package com.tpa.chessengine.controller;

import com.tpa.chessengine.dto.MoveRequest;
import com.tpa.chessengine.dto.MoveResponse;
import com.tpa.chessengine.service.ChessEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/engine")
@RequiredArgsConstructor
public class ChessEngineController {

    private final ChessEngineService chessEngineService;
    @GetMapping
    public ResponseEntity<Set<MoveResponse>> getPossibleMoves(@RequestBody MoveRequest request ){
        return chessEngineService.getPossiblesMoves(request);
    }
}
