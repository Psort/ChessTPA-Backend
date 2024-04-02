package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.Move;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
@Data
public class MoveRequest {
    private String gameId;
    private Move move;
    private String newPawnType;
}
