package com.tpa.gameservice.service;


import com.chesstpa.board.Board;
import com.chesstpa.board.Position;
import com.chesstpa.board.Spot;
import com.chesstpa.communication.ChessEngine;
import com.chesstpa.pieces.PieceColor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.dto.MoveRequest;
import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.dto.SafeGameStateRequest;
import com.tpa.gameservice.model.*;
import com.tpa.gameservice.repository.GameRepository;
import com.tpa.gameservice.type.CastleType;
import com.tpa.gameservice.type.GameStatus;
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
    private final ChessEngine chessEngine;

    @Transactional
    @CircuitBreaker(name = "user-service", fallbackMethod = "fallback")
    public Game createGame(NewGameRequest newGameRequest) {

        Player firstPlayer = Player.builder().username(newGameRequest.getFirstPlayerUsername()).color(PlayerColor.WHITE).build();
        Player secondPlayer = Player.builder().username(newGameRequest.getSecondPlayerUsername()).color(PlayerColor.BLACK).build();

        Game game = createDefaulttdGame( firstPlayer,secondPlayer);

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
            PlayerColor opponentColor  = game.getActualColor() == PlayerColor.WHITE ? PlayerColor.BLACK :PlayerColor.WHITE;
            GameState gameState = GameState.builder()
                    .boardState(safeGameStateRequest.getBoardState())
                    .status( safeGameStateRequest.getGameStatus())
                    .move(safeGameStateRequest.getMove())
                    .castleTypes(safeGameStateRequest.getCastleTypes())
                    .possibleMoves(getPossiblesMoves(safeGameStateRequest.getBoardState(),opponentColor, safeGameStateRequest.getCastleTypes()))
                    .fullMovesCounter(safeGameStateRequest.getFullMovesCounter())
                    .halfMovesCounter(safeGameStateRequest.getHalfMovesCounter())
                    .enPassantPosition(safeGameStateRequest.getEnPassantPosition())
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

    public void updateGameState(MoveRequest moveRequest) {
        Optional<Game> optionalGame = gameRepository.findById(moveRequest.getGameId());
        if (optionalGame.isPresent()){
            Game game = optionalGame.get();
            GameState gameState = game.getHistory().get(game.getHistory().size()-1);
            List<String> castletypes = game.getHistory().get(game.getHistory().size()-1).getCastleTypes();
            if (IsValidMove(gameState.getPossibleMoves(),moveRequest.getMove())){
                String updatedBoardState = updateBoardState(gameState.getBoardState(),moveRequest.getMove());
                List<String> updatedCastleType = calculateCastle(castletypes,moveRequest.getMove().getStartingCoordinates());
                safeGameState(SafeGameStateRequest.builder()
                        .gameId(moveRequest.getGameId())
                        .gameStatus(convertStatus(getGameStatus(updatedBoardState,gameState.getCastleTypes(),game.getActualColor().toString())))
                        .move(moveRequest.getMove())
                        .castleTypes(updatedCastleType)
                        .boardState(updatedBoardState)
                        .enPassantPosition( "todo")
                        .halfMovesCounter(calculateHalfMoves(gameState.getHalfMovesCounter()))
                        .fullMovesCounter(gameState.getFullMovesCounter()+1)
                        .build());
            }
        }
    }

    private int calculateHalfMoves(int halfMovesCounter) {
        return halfMovesCounter+1;
    }

    private String updateBoardState(String boardState, Move move) {
        Board board = new Board();
        board.setBoardState(boardState,"");
        Spot[][] spots = board.getSpots();
        Position startPosition = convertCoordinatestoPosition(move.getStartingCoordinates());
        Position endPosition = convertCoordinatestoPosition(move.getEndingCoordinates());
        Spot spot = spots[startPosition.getX()][startPosition.getY()];
        spots[endPosition.getX()][endPosition.getY()].setPiece(spot.getPiece());
        spot.setPiece(null);
        return board.spotsToBoardState();
    }

    private boolean IsValidMove(Set<PossibleMove> possibleMoves, Move move) {
        Position startPosition = convertCoordinatestoPosition(move.getStartingCoordinates());
        Position endPosition = convertCoordinatestoPosition(move.getEndingCoordinates());
        return possibleMoves.stream()
                .anyMatch(possibleMove ->
                        possibleMove.piecePosition().getX() == startPosition.getX() && possibleMove.piecePosition().getY() == startPosition.getY()
                                &&
                                possibleMove.possibleMovesForPiece().stream()
                                        .anyMatch(position ->
                                                position.getX() == endPosition.getX() && position.getY() == endPosition.getY()));

    }


    private   Position convertCoordinatestoPosition(String coordinates) {
        int x = Integer.parseInt(coordinates.substring(1)) - 1;
        int y = coordinates.toUpperCase().charAt(0) - 'A'; // Przyjmując, że pozycje są w formie "A1", "B2", itp.
        return new Position(x,y);
    }

    private Set<PossibleMove> getPossiblesMoves( String boardState, PlayerColor playerColor, List<String> castles) {
        String convertedCastlesToString = convertCastlesToString(castles);
        Map<Position, List<Position>> allPossiblePosition = chessEngine.getAllPossibleMovesForColor(boardState,playerColor.name(),convertedCastlesToString);
        return  allPossiblePosition.entrySet().stream()
                .map(entry -> new PossibleMove(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }
    private String getGameStatus(String boardState,List<String> castles,String playerColor) {
        String convertCastles = convertCastlesToString(castles);
//        System.out.println(boardState);
//        System.out.println(castles);
//        System.out.println(playerColor);
        return chessEngine.getGameStatus(boardState, convertCastles, playerColor);
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
    private Game createDefaulttdGame(Player firstPlayer,Player secondPlayer){
        int defaultMovesValue = 0;
        String defaultBoardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        List<String> defaultCastleTypes = List.of(CastleType.LONGWHITE.getValue(),
                CastleType.SHORTWHITE.getValue(),
                CastleType.LONGBLACK.getValue(),
                CastleType.SHORTBLACK.getValue());

        GameState defaultGameState = GameState.builder()
                .boardState(defaultBoardState)
                .status(GameStatus.GAME)
                .possibleMoves(getPossiblesMoves(defaultBoardState,PlayerColor.WHITE,defaultCastleTypes))
                .castleTypes(defaultCastleTypes)
                .halfMovesCounter(defaultMovesValue)
                .fullMovesCounter(defaultMovesValue)
                .enPassantPosition("")
                .build();

        return Game.builder()
                .players(new Player[]{firstPlayer, secondPlayer})
                .history(List.of(defaultGameState))
                .actualColor(PlayerColor.WHITE)
                .build();
    }
    private String convertCastlesToString(List<String> castles){
        return String.join("", castles);
    }

    private GameStatus convertStatus(String status){
        return Objects.equals(status, "Checkmate") ? GameStatus.CHECKMATE :
                Objects.equals(status, "Pat") ? GameStatus.PAT :
                        GameStatus.GAME;

    }

    private String fallback(NewGameRequest request, RuntimeException e) {
        logService.sendError( "Error while creating a game");
        return "Can not create game right now, please try again later";
    }
}
