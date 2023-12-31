package com.tpa.gameservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.dto.GameToUserRequest;
import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.dto.SafeGameStateRequest;
import com.tpa.gameservice.model.*;
import com.tpa.gameservice.repository.GameRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    @CircuitBreaker(name = "user-service", fallbackMethod = "fallback")
    public String createGame(NewGameRequest newGameRequest) {
        List<String> defaultCastleTypes = List.of(CastleType.LONGWHITE.getValue(),
                CastleType.SHORTWHITE.getValue(),
                CastleType.LONGBLACK.getValue(),
                CastleType.SHORTBLACK.getValue());

        String defaultBoardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

        GameState defaultGameState = GameState.builder()
                .boardState(defaultBoardState)
                .status(GameStatus.GAME)
                .castleTypes(defaultCastleTypes)
                .build();

        Player firstPlayer = Player.builder().username(newGameRequest.getFirstPlayerUsername()).color(PlayerColor.WHITE).build();
        Player secondPlayer = Player.builder().username(newGameRequest.getSecondPlayerUsername()).color(PlayerColor.BLACK).build();

        Game game = Game.builder()
                .players(new Player[]{firstPlayer, secondPlayer})
                .history(List.of(defaultGameState))
                .actualColor(PlayerColor.WHITE)
                .build();

        gameRepository.save(game);

        sendGameToUserService(game.getId(), firstPlayer.getUsername(), secondPlayer.getUsername());

        return game.getId();
    }

    public List<GameResponse> getAllGamesForUser(String username) {
        List<Game> optionalGames = gameRepository.findByPlayersUsernameAndHistoryStatusIsNotIn(username,List.of("CHECKMATE","PAT"));
        List<GameResponse> games = new ArrayList<>();
        for (Game game:optionalGames) {
            games.add(GameResponse.builder()
                    .id(game.getId()).actualColor(game.getActualColor())
                    .players(game.getPlayers())
                    .history(game.getHistory())
                    .build());
        }
        return games;
    }

    public GameResponse getGame(String gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isPresent()) {

            GameResponse game = GameResponse.builder()
                    .id(optionalGame.get().getId())
                    .players(optionalGame.get().getPlayers())
                    .history(optionalGame.get().getHistory())
                    .actualColor(optionalGame.get().getActualColor())
                    .build();

        return game;
    }
        else return null;
    }

    public String getGameResponseAsJson(String gameId) {
        return convertToGameResponseAsJson(gameId);
    }

    private String convertToGameResponseAsJson(String gameId) {
        GameResponse gameResponse = getGame(gameId);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(gameResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void safeGameState(SafeGameStateRequest safeGameStateRequest) {
        Optional<Game> optionalGame = gameRepository.findById(safeGameStateRequest.getGameId());

        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();

            GameState gameState = GameState.builder()
                    .boardState(safeGameStateRequest.getBoardState())
                    .status( GameStatus.getGameStatusByValue(safeGameStateRequest.getGameStatus()))
                    .move(safeGameStateRequest.getMove())
                    .castleTypes(
                            calculateCastle(
                                    game.getHistory().get(game.getHistory().size()-1).getCastleTypes()
                                    ,safeGameStateRequest.getMove().getStartingCoordinates()))
                    .build();

            PlayerColor actualColor = (game.getActualColor().equals(PlayerColor.WHITE)) ? PlayerColor.BLACK : PlayerColor.WHITE;
            game.setActualColor(actualColor);
            game.addGameStateToHistory(gameState);
            gameRepository.save(game);
        }
    }

    private List<String> calculateCastle(List<String> castleTypes, String startingCoordinates) {
        switch (startingCoordinates) {
            case "a1" -> castleTypes.remove("q");
            case "h1" -> castleTypes.remove("k");
            case "a8" -> castleTypes.remove("K");
            case "h8" -> castleTypes.remove("Q");
            case "e1" -> castleTypes.removeAll(List.of("k", "q"));
            case "e8" -> castleTypes.removeAll(List.of("K", "Q"));
        }
        return castleTypes;
    }

    private void sendGameToUserService(String gameId, String firstPlayerUsername, String secondPlayerUsername) {

        GameToUserRequest request = GameToUserRequest.builder()
                .gameId(gameId)
                .firstPlayerUsername(firstPlayerUsername)
                .secondPlayerUsername(secondPlayerUsername)
                .build();

        webClientBuilder.build().post().uri("http://user-service/api/user/game")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<String> getMovesHistory(String gameId){
        Optional<Game> optionalGame = gameRepository.findById(gameId);

        if(optionalGame.isPresent()) {
            Game game = optionalGame.get();

            return game.getHistory().stream()
                    .skip(1)
                    .map(gameState -> gameState.getMove().getEndingCoordinates())
                    .toList();
        }

        //todo
        return null;
    }
    private String fallback(NewGameRequest request, RuntimeException e) {
        return "Can not create game right now, please try again later";
    }
}
