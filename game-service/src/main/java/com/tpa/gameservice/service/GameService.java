package com.tpa.gameservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.gameservice.dto.*;
import com.tpa.gameservice.model.*;
import com.tpa.gameservice.repository.GameRepository;
import com.tpa.gameservice.type.CastleType;
import com.tpa.gameservice.type.GameStatus;
import com.tpa.gameservice.type.GameType;
import com.tpa.gameservice.type.PlayerColor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GameService {
    private final LogService logService;
    private final WebClientService webClientService;
    private final GameRepository gameRepository;
    private final ChessEngineService chessEngineService;

    @Transactional
    @CircuitBreaker(name = "user-service", fallbackMethod = "fallback")
    public Game createGame(NewGameRequest newGameRequest) {

        Player firstPlayer = Player.builder().username(newGameRequest.getFirstPlayerUsername()).color(PlayerColor.WHITE).build();
        Player secondPlayer = Player.builder().username(newGameRequest.getSecondPlayerUsername()).color(PlayerColor.BLACK).build();
        Game game=createDefaulttdGame(firstPlayer, secondPlayer, newGameRequest.getGameType());

        Game game = createDefaultGame(firstPlayer, secondPlayer, newGameRequest.getGameType());

        gameRepository.save(game);

        webClientService.sendGameToUserService(game.getId(), firstPlayer.getUsername(), secondPlayer.getUsername());

        return game;
    }


    public List<GameResponse> getAllGamesForUser(String username) {
        List<Game> optionalGames = gameRepository.findByPlayersUsernameAndHistoryStatusIsNotIn(username,List.of("CHECKMATE","PAT"));
        return optionalGames.stream().map(game -> GameResponse.builder()
                .id(game.getId())
                .actualColor(game.getActualColor())
                .players(game.getPlayers())
                .history(game.getHistory()).build()).toList();
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
            PlayerColor actualColor = (game.getActualColor().equals(PlayerColor.WHITE)) ? PlayerColor.BLACK : PlayerColor.WHITE;
            GameState gameState = createGameState(safeGameStateRequest, actualColor);
            game.setActualColor(actualColor);
            game.addGameStateToHistory(gameState);
            gameRepository.save(game);
        }
    }

    private GameState createGameState(SafeGameStateRequest safeGameStateRequest, PlayerColor actualColor) {
        Set<PossibleMove> possibleMoves = chessEngineService.getPossiblesMoves(
                safeGameStateRequest.getBoardState(),
                actualColor,
                safeGameStateRequest.getEnPassantPosition(),
                safeGameStateRequest.getCastleTypes());
        return GameState.builder()
                .boardState(safeGameStateRequest.getBoardState())
                .status(safeGameStateRequest.getGameStatus())
                .move(safeGameStateRequest.getMove())
                .castleTypes(safeGameStateRequest.getCastleTypes())
                .possibleMoves(possibleMoves)
                .fullMovesCounter(safeGameStateRequest.getFullMovesCounter())
                .halfMovesCounter(safeGameStateRequest.getHalfMovesCounter())
                .enPassantPosition(safeGameStateRequest.getEnPassantPosition())
                .build();
    }

    public List<String> getMovesHistory(String gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        return optionalGame.map(game -> game.getHistory().stream()
                        .skip(1)
                        .map(gameState -> gameState.getMove().getEndingCoordinates())
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    public void updateGameState(MoveRequest moveRequest) {
        Optional<Game> optionalGame = gameRepository.findById(moveRequest.getGameId());
        if (optionalGame.isPresent()){
            Game game = optionalGame.get();
            SafeGameStateRequest safeGameStateRequest = chessEngineService.getUpdatedSafeGameStateRequest(moveRequest, game);
            safeGameState(safeGameStateRequest);
            if(game.getGameType() == GameType.COMPUTER){
                Game nextGame = gameRepository.findById(moveRequest.getGameId()).get();
                String username = getHumanPlayer(game).getUsername();
                Double eloRating =  webClientService.getUserEloRating(username);
                ComputerMoveRequest computerMoveRequest = createComputerMoveRequest(safeGameStateRequest,game,eloRating);
                String move = webClientService.getComputerMove(computerMoveRequest);
                MoveRequest nextMoveRequest =  convertMoveToMoveRequest(move);
                nextMoveRequest.setGameId(nextGame.getId());
                SafeGameStateRequest nextSafeGameStateRequest =chessEngineService.getUpdatedSafeGameStateRequest(nextMoveRequest, nextGame);
                safeGameState(nextSafeGameStateRequest);
            }
        }
    }

    private MoveRequest convertMoveToMoveRequest(String stringMove) {
        int halfLength = stringMove.length() / 2;

        String startingCoordinates = stringMove.substring(0, halfLength);
        String endingCoordinates = stringMove.substring(halfLength);

        Move move = Move.builder().startingCoordinates(startingCoordinates).endingCoordinates(endingCoordinates).build();
        return MoveRequest.builder().move(move).build();
    }

    private static Player getHumanPlayer(Game game) {
        return Arrays.stream(game.getPlayers())
                .filter(p -> !Objects.equals(p.getUsername(), "COMPUTER"))
                .findFirst()
                .orElse(null);
    }

    private ComputerMoveRequest createComputerMoveRequest(SafeGameStateRequest safeGameStateRequest, Game game,Double eloRating) {
        String color = game.getActualColor() == PlayerColor.WHITE ? "b": "w";
        StringBuilder fenBody = new StringBuilder();
        String castle = Objects.equals(chessEngineService.convertCastlesToString(safeGameStateRequest.getCastleTypes()), "") ? "-":chessEngineService.convertCastlesToString(safeGameStateRequest.getCastleTypes());
        String enPassantPosition = Objects.equals(safeGameStateRequest.getEnPassantPosition(), "") ? "-" : safeGameStateRequest.getEnPassantPosition();
        fenBody.append(safeGameStateRequest.getBoardState());
        fenBody.append(" ");
        fenBody.append(color);
        fenBody.append(" ");
        fenBody.append(castle);
        fenBody.append(" ");
        fenBody.append(enPassantPosition);
        fenBody.append(" ");
        fenBody.append(safeGameStateRequest.getHalfMovesCounter());
        fenBody.append(" ");
        fenBody.append(safeGameStateRequest.getFullMovesCounter());
        return ComputerMoveRequest.builder().eloRating(eloRating.toString()).fenBody(fenBody.toString()).build();
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
    private Game createDefaultGame(Player firstPlayer, Player secondPlayer, GameType gameType) {
        Double defaultTimeInSeconds = (double) (gameType.getMinutes() * 60);

    private Game createDefaulttdGame(Player firstPlayer, Player secondPlayer, GameType gameType){
            Double defaultTimeInSeconds = (double) (gameType.getMinutes() * 60);
        GameState defaultGameState = createDefaulGameState();
        return Game.builder()
                .players(new Player[]{firstPlayer, secondPlayer})
                .history(List.of(defaultGameState))
                .gameType(gameType)
                .actualColor(PlayerColor.WHITE)
                .whitePlayerTime(defaultTimeInSeconds)
                .blackPlayerTime(defaultTimeInSeconds)
                .build();
    }

    private GameState createDefaulGameState() {
        int defaultMovesValue = 0;
        String defaultBoardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        List<String> defaultCastleTypes = createDefaultCastleType();
        return GameState.builder()
                .boardState(defaultBoardState)
                .status(GameStatus.GAME)
                .possibleMoves(chessEngineService.getPossiblesMoves(defaultBoardState,PlayerColor.WHITE,"", defaultCastleTypes))
                .castleTypes(defaultCastleTypes)
                .halfMovesCounter(defaultMovesValue)
                .fullMovesCounter(defaultMovesValue)
                .enPassantPosition("")
                .build();
    }

    private static List<String> createDefaultCastleType() {
        return List.of(CastleType.LONGWHITE.getValue(),
                CastleType.SHORTWHITE.getValue(),
                CastleType.LONGBLACK.getValue(),
                CastleType.SHORTBLACK.getValue());
    }

    private String fallback(NewGameRequest request, RuntimeException e) {
        logService.sendError( "Error while creating a game");
        return "Can not create game right now, please try again later";
    }
}
