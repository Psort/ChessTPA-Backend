package com.tpa.gameservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GameToUserRequest {
    private String gameId;
    private String firstPlayerUsername;
    private String secondPlayerUsername;
}
