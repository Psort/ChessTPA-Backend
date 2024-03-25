package com.tpa.gameservice.model;

import com.chesstpa.board.Position;

import java.util.List;

public record PossibleMove(
        Position piecePosition,
        List<Position> possibleMovesForPiece
) {
}
