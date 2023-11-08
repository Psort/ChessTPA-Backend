package com.tpa.gameservice.service;


import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.model.Game;
import com.tpa.gameservice.model.GameState;
import com.tpa.gameservice.model.Player;
import com.tpa.gameservice.repository.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    public ResponseEntity<String> createGame(NewGameRequest newGameRequest) {
        Player firstPlayer = Player.builder().name(newGameRequest.getFirstPlayerId()).build();
        Player secondPlayer = Player.builder().name(newGameRequest.getSecondPlayerId()).build();
        Game game = Game.builder().players(new Player[]{firstPlayer, secondPlayer}).build();
        gameRepository.save(game);
        return new ResponseEntity<>(game.getId(), HttpStatus.CREATED);
    }
}
