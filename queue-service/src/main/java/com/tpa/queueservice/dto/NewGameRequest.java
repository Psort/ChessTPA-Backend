package com.tpa.queueservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NewGameRequest {
    String firstPlayerId;
    String secondPlayerId;
}
