package com.tpa.gameservice.model;

import com.tpa.gameservice.type.GameType;
import com.tpa.gameservice.type.PlayerColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Double whitePlayerTime;
    private Double blackPlayerTime;
    public void addGameStateToHistory(GameState gameState) {
        history.add(gameState);
    }
}
