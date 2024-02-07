package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.Move;
import lombok.Data;

@Data
public class SafeGameStateRequest {
    String gameId;
    String gameStatus;
    Move move;
    String boardState;
}
