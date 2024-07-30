package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData(String gameName) {
        this(0, null, null, gameName, null);
    }

    public GameData(String gameName, ChessGame game) {
        this(0, null, null, gameName, game);
    }
}
