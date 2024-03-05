package com.tpa.gameservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Document(value = "game")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Game {
    @Id
    private String id;
    private List<GameState> history;
    private Player[] players;
    private PlayerColor actualColor;
    private GameType gameType;
    private Duration whitePlayerTime;
    private Duration blackPlayerTime;
    public void addGameStateToHistory(GameState gameState) {
        history.add(gameState);
    }
}
