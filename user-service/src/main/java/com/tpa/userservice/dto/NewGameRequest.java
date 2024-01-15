package com.tpa.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewGameRequest {
    private String gameId;
    private String firstPlayerUsername;
    private String secondPlayerUsername;
}
