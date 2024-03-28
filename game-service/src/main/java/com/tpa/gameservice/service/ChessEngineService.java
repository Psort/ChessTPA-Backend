package com.tpa.gameservice.service;

import com.chesstpa.board.Board;
import com.chesstpa.board.Position;
import com.chesstpa.board.Spot;
import com.chesstpa.communication.ChessEngine;
import com.chesstpa.pieces.PieceColor;
import com.chesstpa.pieces.Rook;
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
    private String convertCastlesToString(List<String> castles){
        return String.join("", castles);
    }

    private String updateBoardState(String boardState,String enPassantPosition, Move move) {
        Board board = new Board();
        board.setBoardState(boardState,"");
        Position startPosition = convertCoordinatesToPosition(move.getStartingCoordinates());
        Position endPosition = convertCoordinatesToPosition(move.getEndingCoordinates());
        Spot spot = board.getSpot(startPosition.getX(),startPosition.getY());
        board.getSpot(endPosition.getX(),endPosition.getY()).setPiece(spot.getPiece());
        updateEnPassantPosition(enPassantPosition, move, board, endPosition);
        updateCastle(spot, startPosition, endPosition, board);
        spot.setPiece(null);

        return board.spotsToBoardState();
    }

    private static void updateCastle(Spot spot, Position startPosition, Position endPosition, Board board) {
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
        if (IsValidMove(gameState.getPossibleMoves(), moveRequest.getMove())){
            List<String> castleTypes = game.getHistory().get(game.getHistory().size()-1).getCastleTypes();
            List<String> updatedCastleType = calculateCastle(castleTypes, moveRequest.getMove().getStartingCoordinates());
            String updatedBoardState = updateBoardState(gameState.getBoardState(),gameState.getEnPassantPosition(), moveRequest.getMove());

            return SafeGameStateRequest.builder()
                    .gameId(moveRequest.getGameId())
                    .gameStatus(convertStatus(getGameStatus(updatedBoardState,gameState.getCastleTypes(),gameState.getEnPassantPosition(), game.getActualColor().toString())))
                    .move(moveRequest.getMove())
                    .castleTypes(updatedCastleType)
                    .boardState(updatedBoardState)
                    .enPassantPosition( calculateEnPassantPosition(updatedBoardState,moveRequest.getMove()))
                    .halfMovesCounter(calculateHalfMoves(gameState.getHalfMovesCounter()))
                    .fullMovesCounter(gameState.getFullMovesCounter()+1)
                    .build();
        }
        return null; // Todo --------------------------------------------------------------------------------------------------------------------
    }

    private String calculateEnPassantPosition(String boardState, Move move) {
        Board board = new Board();
        board.setBoardState(boardState,"");
        Position startPosition = convertCoordinatesToPosition(move.getStartingCoordinates());
        Position endPosition = convertCoordinatesToPosition(move.getEndingCoordinates());
        Spot spotPiece = board.getSpot(endPosition.getX(),endPosition.getY());
        boolean startMove = ((startPosition.getX() == 1 && endPosition.getX() == 3)|| (startPosition.getX() == 6 && endPosition.getX() == 4));
        if (spotPiece != null && spotPiece.getPiece().getSimpleName() == 'p' && startMove) {
            int enPassantX = (spotPiece.getPiece().getColor() == PieceColor.BLACK) ? endPosition.getX() - 1 : endPosition.getX() + 1;
            return convertPositionToString(endPosition.getY(), enPassantX);
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
            case "a1" -> castleTypes.remove("q");
            case "h1" -> castleTypes.remove("k");
            case "a8" -> castleTypes.remove("K");
            case "h8" -> castleTypes.remove("Q");
            case "e1" -> castleTypes.removeAll(List.of("k", "q"));
            case "e8" -> castleTypes.removeAll(List.of("K", "Q"));
        }
        return castleTypes;
    }
    private int calculateHalfMoves(int halfMovesCounter) {
        return halfMovesCounter+1;
    }//Todo----------------------------------------------------------------------
    public String convertPositionToString(int x, int y) {
        char letter = (char) ('a' + x);
        int number = y + 1;
        return String.valueOf(letter) + number;
    }
}
