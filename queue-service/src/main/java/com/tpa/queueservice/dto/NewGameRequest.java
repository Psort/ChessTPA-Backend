package com.tpa.queueservice.dto;

import com.tpa.queueservice.type.GameType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class NewGameRequest {
    String firstPlayerUsername;
    String secondPlayerUsername;
    GameType gameType;
}
