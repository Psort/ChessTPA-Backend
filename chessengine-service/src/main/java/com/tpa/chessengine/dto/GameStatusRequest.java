package com.tpa.chessengine.dto;

import lombok.Getter;

@Getter
public class GameStatusRequest {
    private String boardState;
    private String[] castles;
    private String color;
}
