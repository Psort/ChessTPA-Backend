package com.tpa.chessengine.dto;

import com.chesstpa.pieces.PieceColor;
import lombok.Getter;

@Getter
public class MoveRequest {
    private String boardState;
    private PieceColor playerColor;
    private String[] castles;
}
