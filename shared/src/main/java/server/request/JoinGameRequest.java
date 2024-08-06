package server.request;

import chess.ChessGame;

public class JoinGameRequest {
    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    private String username = "";

    public JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameId() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}
