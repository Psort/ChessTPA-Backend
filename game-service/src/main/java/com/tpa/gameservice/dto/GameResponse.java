package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.Game;
import com.tpa.gameservice.model.GameState;
import com.tpa.gameservice.model.Player;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GameResponse {
    String id;
    List<GameState> history;
    Player[] players;
}
