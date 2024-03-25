package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.Move;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MoveRequest {
    private String gameId;
    Move move;
}
