package com.tpa.gameservice.service;

import com.chesstpa.board.Board;
import com.chesstpa.board.Position;
import com.chesstpa.board.Spot;
import com.chesstpa.communication.ChessEngine;
import com.chesstpa.pieces.*;
import com.tpa.gameservice.dto.MoveRequest;
import com.tpa.gameservice.dto.SafeGameStateRequest;
import com.tpa.gameservice.model.Game;
import com.tpa.gameservice.model.GameState;
import com.tpa.gameservice.model.Move;
import com.tpa.gameservice.model.PossibleMove;
import com.tpa.gameservice.type.GameStatus;
import com.tpa.gameservice.type.PlayerColor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChessEngineService {
    private final ChessEngine chessEngine;
    private Position convertCoordinatesToPosition(String coordinates) {
        int x = Integer.parseInt(coordinates.substring(1)) - 1;
        int y = coordinates.toUpperCase().charAt(0) - 'A'; // Przyjmując, że pozycje są w formie "A1", "B2", itp.
        return new Position(x,y);
    }

    protected Set<PossibleMove> getPossiblesMoves(String boardState, PlayerColor playerColor,String enPassantPosition, List<String> castles) {
        String convertedCastlesToString = convertCastlesToString(castles);
        Map<Position, List<Position>> allPossiblePosition = chessEngine.getAllPossibleMovesForColor(boardState,playerColor.name(),enPassantPosition,convertedCastlesToString);
        return  allPossiblePosition.entrySet().stream()
                .map(entry -> new PossibleMove(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }
    private String getGameStatus(String boardState,List<String> castles,String enPassantPosition,String playerColor) {
        String convertCastles = convertCastlesToString(castles);
        return chessEngine.getGameStatus(boardState, convertCastles,enPassantPosition, playerColor);
    }
    String convertCastlesToString(List<String> castles){
        return String.join("", castles);
    }

    private String updateBoardState(String boardState,String enPassantPosition, Move move,String newPawnType) {
        Board board = new Board();
        board.setBoardState(boardState,"");
        Position startPosition = convertCoordinatesToPosition(move.getStartingCoordinates());
        Position endPosition = convertCoordinatesToPosition(move.getEndingCoordinates());
        Spot spot = board.getSpot(startPosition.getX(),startPosition.getY());

        board.getSpot(endPosition.getX(),endPosition.getY()).setPiece(getNewPice(spot.getPiece(),newPawnType));
        updateEnPassantPosition(enPassantPosition, move, board, endPosition);
        doCastleMove(spot, startPosition, endPosition, board);
        spot.setPiece(null);

        return board.spotsToBoardState();
    }

    private Piece getNewPice(Piece piece, String newPawnType) {
        PieceColor color = piece.getColor();
        return switch (newPawnType) {
            case "Q" -> new Queen(color);
            case "R" -> new Rook(color);
            case "N" -> new Knight(color);
            case "B" -> new Bishop(color);
            default -> piece;
        };
    }

    private static void doCastleMove(Spot spot, Position startPosition, Position endPosition, Board board) {
        if(spot.getPiece().getSimpleName() == 'k'){
            if (startPosition.getY() ==4 && endPosition.getY() == 6){
                board.getSpot(endPosition.getX(), endPosition.getY()+1).setPiece(null);
                board.getSpot(endPosition.getX(), endPosition.getY()-1).setPiece(new Rook(spot.getPiece().getColor()));
            }
            if (startPosition.getY() ==4 && endPosition.getY() == 2){
                board.getSpot(endPosition.getX(), endPosition.getY()-2).setPiece(null);
                board.getSpot(endPosition.getX(), endPosition.getY()+1).setPiece(new Rook(spot.getPiece().getColor()));
            }
        }
    }

    private static void updateEnPassantPosition(String enPassantPosition, Move move, Board board, Position endPosition) {
        int direction = (board.getSpot(endPosition.getX(), endPosition.getY()).getPiece().getColor() == PieceColor.WHITE) ? 1 : -1;
        if(Objects.equals(enPassantPosition, move.getEndingCoordinates())){
            board.getSpot(endPosition.getX()+direction, endPosition.getY()).setPiece(null);
        }
    }

    private boolean IsValidMove(Set<PossibleMove> possibleMoves, Move move) {
        Position startPosition = convertCoordinatesToPosition(move.getStartingCoordinates());
        Position endPosition = convertCoordinatesToPosition(move.getEndingCoordinates());
//        System.out.println(move.getStartingCoordinates()+move.getEndingCoordinates());
//        System.out.println("start");
//        System.out.println(startPosition.getX() + " "+ startPosition.getY());
//        System.out.println("end");
//        System.out.println(endPosition.getX() + " "+ endPosition.getY());
//        System.out.println("posible");
//        possibleMoves.forEach(possibleMove -> {
//            System.out.println(possibleMove.piecePosition().getX()+ ""+ possibleMove.piecePosition().getY() +":" );
//            possibleMove.possibleMovesForPiece().forEach(position -> {
//                System.out.println(position.getX() +" " + position.getY());
//            });
//        });

        return possibleMoves.stream()
                .anyMatch(possibleMove ->
                        possibleMove.piecePosition().getX() == startPosition.getX() &&
                                possibleMove.piecePosition().getY() == startPosition.getY() &&
                                possibleMove.possibleMovesForPiece().stream()
                                        .anyMatch(position ->
                                                position.getX() == endPosition.getX() && position.getY() == endPosition.getY()));

    }
    protected SafeGameStateRequest getUpdatedSafeGameStateRequest(MoveRequest moveRequest, Game game) {
        GameState gameState = game.getHistory().get(game.getHistory().size()-1);
        System.out.println(gameState);
        if (IsValidMove(gameState.getPossibleMoves(), moveRequest.getMove())){
            List<String> castleTypes = game.getHistory().get(game.getHistory().size()-1).getCastleTypes();
            List<String> updatedCastleType = calculateCastle(castleTypes, moveRequest.getMove().getStartingCoordinates());
            String updatedBoardState = updateBoardState(gameState.getBoardState(),gameState.getEnPassantPosition(), moveRequest.getMove() ,moveRequest.getNewPawnType());
            PlayerColor opponentColor = (game.getActualColor().equals(PlayerColor.WHITE)) ? PlayerColor.BLACK : PlayerColor.WHITE;
            GameStatus gameStatus = convertStatus(getGameStatus(updatedBoardState,gameState.getCastleTypes(),gameState.getEnPassantPosition(), opponentColor.toString()));
            return SafeGameStateRequest.builder()
                    .gameId(moveRequest.getGameId())
                    .gameStatus(gameStatus)
                    .move(moveRequest.getMove())
                    .castleTypes(updatedCastleType)
                    .boardState(updatedBoardState)
                    .enPassantPosition( calculateEnPassantPosition(updatedBoardState,moveRequest.getMove()))
                    .halfMovesCounter(calculateHalfMoves(gameState.getHalfMovesCounter(),gameState.getBoardState(),updatedBoardState,moveRequest.getMove()))
                    .fullMovesCounter(gameState.getFullMovesCounter()+1)
                    .build();
        }
        return null; // Todo --------------------------------------------------------------------------------------------------------------------
    }

    private int calculateHalfMoves(int halfMovesCounter, String boardState, String updatedBoardState, Move move) {
        Position endPosition = convertCoordinatesToPosition(move.getEndingCoordinates());
        Board board = new Board();
        Board updatedBoard = new Board();
        board.setBoardState(boardState, "");
        updatedBoard.setBoardState(updatedBoardState, "");
        Spot spot = board.getSpot(endPosition.getX(), endPosition.getY());
        Spot updatedSpot = updatedBoard.getSpot(endPosition.getX(), endPosition.getY());


        if (updatedSpot.getPiece().getSimpleName() =='p') {
            return 0;
        }
        if (spot.getPiece() != null && updatedSpot.getPiece() != null &&
                updatedSpot.getPiece().getColor() != spot.getPiece().getColor()) {
            return 0;
        }
        switch (move.getStartingCoordinates()) {
            case "a1", "h1", "a8", "h8", "e1", "e8":
                return 0;
        }

        return halfMovesCounter + 1;
    }


    private String calculateEnPassantPosition(String boardState, Move move) {
        Board board = new Board();
        board.setBoardState(boardState,"");
        Position startPosition = convertCoordinatesToPosition(move.getStartingCoordinates());
        Position endPosition = convertCoordinatesToPosition(move.getEndingCoordinates());
        Spot spotPiece = board.getSpot(endPosition.getX(),endPosition.getY());
        boolean startMove = ((startPosition.getX() == 1 && endPosition.getX() == 3)|| (startPosition.getX() == 6 && endPosition.getX() == 4));
        if (spotPiece != null && spotPiece.getPiece().getSimpleName() == 'p' && startMove) {
            int enPassantY = (spotPiece.getPiece().getColor() == PieceColor.BLACK) ? endPosition.getY() + 1 : endPosition.getY() - 1;
            return convertPositionToString( endPosition.getX(),enPassantY);
        }
        return "";
    }

    private GameStatus convertStatus(String status){
        return Objects.equals(status, "Checkmate") ? GameStatus.CHECKMATE :
                Objects.equals(status, "Pat") ? GameStatus.PAT :
                        GameStatus.GAME;

    }
    private List<String> calculateCastle(List<String> castleTypes, String startingCoordinates) {
        switch (startingCoordinates) {
            case "a1" -> castleTypes.remove("Q");
            case "h1" -> castleTypes.remove("K");
            case "a8" -> castleTypes.remove("k");
            case "h8" -> castleTypes.remove("q");
            case "e1" -> castleTypes.removeAll(List.of("K", "Q"));
            case "e8" -> castleTypes.removeAll(List.of("k", "q"));
        }
        return castleTypes;
    }
    public String convertPositionToString(int x, int y) {
        int number = x + 1;
        char letter = (char) ('a' + y);
        return String.valueOf(letter) + number;
    }

}
