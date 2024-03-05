package com.tpa.gameservice.dto;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class GameToUserRequest {
    private String gameId;
    private String firstPlayerUsername;
    private String secondPlayerUsername;
}
