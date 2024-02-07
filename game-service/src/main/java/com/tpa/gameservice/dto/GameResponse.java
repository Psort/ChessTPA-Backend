package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GameResponse {
    private String id;
    private List<GameState> history;
    private Player[] players;
    private PlayerColor actualColor;
    private GameType gameType;
}
