package com.tpa.chessengine.dto;

import lombok.Getter;

@Getter
public class MoveRequest {
    private String boardState;
    private String piecePosition;
    private String whiteCastle;
    private String blackCastle;
}
