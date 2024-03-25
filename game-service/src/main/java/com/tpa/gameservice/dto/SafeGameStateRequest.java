package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.Move;
import com.tpa.gameservice.type.GameStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SafeGameStateRequest {
    String gameId;
    GameStatus gameStatus;
    Move move;
    List<String> castleTypes;
    String boardState;
    String enPassantPosition;
    int halfMovesCounter;
    int fullMovesCounter;
}
