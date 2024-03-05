package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.GameType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewGameRequest {
    String firstPlayerUsername;
    String secondPlayerUsername;
    GameType gameType;
}
