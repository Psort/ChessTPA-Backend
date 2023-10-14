package com.tpa.chessengine.controller;

import com.tpa.chessengine.model.Move;
import com.tpa.chessengine.service.ChessEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/engine")
@RequiredArgsConstructor
public class ChessEngineController {

    private final ChessEngineService chessEngineService;
    @GetMapping
    public Set<Move> getPossibleMoves(){

        return chessEngineService.getPossiblesMoves("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR","g4");
    }
}
