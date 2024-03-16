package com.tpa.gameservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.dto.SafeGameStateRequest;
import com.tpa.gameservice.model.*;
import com.tpa.gameservice.repository.GameRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@AllArgsConstructor
public class GameService {
    private final LogService logService;
    private final WebClientService webClientService;
    private final GameRepository gameRepository;

    @Transactional
    @CircuitBreaker(name = "user-service", fallbackMethod = "fallback")
    public Game createGame(NewGameRequest newGameRequest) {

        Player firstPlayer = Player.builder().username(newGameRequest.getFirstPlayerUsername()).color(PlayerColor.WHITE).build();
        Player secondPlayer = Player.builder().username(newGameRequest.getSecondPlayerUsername()).color(PlayerColor.BLACK).build();

        Game game = createDefaultGame(firstPlayer, secondPlayer, newGameRequest);

        gameRepository.save(game);

        webClientService.sendGameToUserService(game.getId(), firstPlayer.getUsername(), secondPlayer.getUsername());

        return game;
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
        return optionalGame.map(game -> GameResponse.builder()
                .id(game.getId())
                .players(game.getPlayers())
                .history(game.getHistory())
                .actualColor(game.getActualColor())
                .build()).orElse(null);
    }

    public String getGameResponseAsJson(String gameId) {
        GameResponse gameResponse = getGame(gameId);
        return convertToGameResponseAsJson(gameResponse);
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

    private String convertToGameResponseAsJson(GameResponse gameResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(gameResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
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

    private Game createDefaultGame(Player firstPlayer, Player secondPlayer, NewGameRequest request){
        List<String> defaultCastleTypes = List.of(CastleType.LONGWHITE.getValue(),
                CastleType.SHORTWHITE.getValue(),
                CastleType.LONGBLACK.getValue(),
                CastleType.SHORTBLACK.getValue());

        String defaultBoardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

        int timeInSeconds = request.getGameType().getMinutes() * 60;

        GameState defaultGameState = GameState.builder()
                .boardState(defaultBoardState)
                .status(GameStatus.GAME)
                .castleTypes(defaultCastleTypes)
                .whitePlayerTimeLeft(timeInSeconds)
                .blackPlayerTimeLeft(timeInSeconds)
                .build();

        return Game.builder()
                .players(new Player[]{firstPlayer, secondPlayer})
                .history(List.of(defaultGameState))
                .actualColor(PlayerColor.WHITE)
                .build();
    }
    private String fallback(NewGameRequest request, RuntimeException e) {
        logService.sendError( "Error while creating a game");
        return "Can not create game right now, please try again later";
    }

}
