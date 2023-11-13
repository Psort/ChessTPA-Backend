package com.tpa.queueservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class NewGameRequest {
    String firstPlayerUsername;
    String secondPlayerUsername;
}
