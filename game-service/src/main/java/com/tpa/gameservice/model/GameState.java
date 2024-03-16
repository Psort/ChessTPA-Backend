package com.tpa.gameservice.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Builder
@Getter
public class GameState {
    GameStatus status;
    Move move;
    String boardState;
    List<String> castleTypes;
    Integer whitePlayerTimeLeft;
    Integer blackPlayerTimeLeft;
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if(!(that instanceof GameState thatResponse)) return false;
        return Objects.equals(this.status, thatResponse.status) && Objects.equals(this.move, thatResponse.move) && this.boardState.equals(thatResponse.boardState) && this.castleTypes.equals(thatResponse.castleTypes);
    }
}
