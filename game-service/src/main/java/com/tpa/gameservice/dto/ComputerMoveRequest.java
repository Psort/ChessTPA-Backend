package com.tpa.gameservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ComputerMoveRequest {
    private String eloRating;
    private String fenBody;
}
