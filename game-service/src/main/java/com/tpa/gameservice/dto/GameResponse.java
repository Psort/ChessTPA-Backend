package com.tpa.gameservice.dto;

import com.tpa.gameservice.model.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
public class GameResponse {
    private String id;
    private List<GameState> history;
    private Player[] players;
    private PlayerColor actualColor;

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if(!(that instanceof GameResponse thatResponse)) return false;
        return Objects.equals(this.history, thatResponse.history) && Arrays.equals(this.players, thatResponse.players) && Objects.equals(this.actualColor, thatResponse.actualColor) ;
    }
    private GameType gameType;
}
