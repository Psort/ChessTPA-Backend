package com.tpa.gameservice.service;


import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.model.*;
import com.tpa.gameservice.repository.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    public ResponseEntity<String> createGame(NewGameRequest newGameRequest) {
        List<CastleType> defaultCastleTypes = List.of(CastleType.LONGWHITE,CastleType.SHORTWHITE,CastleType.LONGBLACK,CastleType.SHORTBLACK);
        String defaultBoardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        GameState defaultGameState = GameState.builder()
                .boardState(defaultBoardState)
                .status(GameStatus.PLAY)
                .castleTypes(defaultCastleTypes)
                .build();
        Player firstPlayer = Player.builder().username(newGameRequest.getFirstPlayerUsername()).build();
        Player secondPlayer = Player.builder().username(newGameRequest.getSecondPlayerUsername()).build();
        System.out.println(newGameRequest.getFirstPlayerUsername());
        System.out.println(newGameRequest.getSecondPlayerUsername());
        Game game = Game.builder()
                .players(new Player[]{firstPlayer, secondPlayer})
                .history(List.of(defaultGameState))
                .build();
        gameRepository.save(game);
        return new ResponseEntity<>(game.getId(), HttpStatus.CREATED);
    }

    public ResponseEntity<GameResponse> getGame(String gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        GameResponse game =  GameResponse.builder()
                .id(optionalGame.get().getId())
                .players(optionalGame.get().getPlayers())
                .history(optionalGame.get().getHistory())
                .build();
        return ResponseEntity.ok(game);
    }
}
