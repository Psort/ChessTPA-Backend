package com.tpa.gameservice.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Builder
@Getter
public class GameState {
    GameStatus status;
    Move move;
    String boardState;
    List<CastleType> castleTypes;
}
