package com.tpa.chessengine.dto;

import com.chesstpa.board.Position;

import java.util.List;

public record MoveResponse(
        Position myPosition,
        List<Position> possibleMoves
) {
}
