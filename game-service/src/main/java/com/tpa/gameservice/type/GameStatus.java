package com.tpa.gameservice.type;

public enum GameStatus {
    PAT,
    CHECKMATE,
    GAME;

    public static GameStatus getGameStatusByValue(String gameStatus) {
        return switch (gameStatus) {
            case "PAT" -> PAT;
            case "CHECKMATE" -> CHECKMATE;
            case "GAME" -> GAME;
            default -> throw new IllegalArgumentException("Invalid game status: " + gameStatus);
        };
    }
}