package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.Map;

public class LoadGameMessage extends ServerMessage {
    private final Map<ChessPosition, ChessPiece> game;
    private final ChessGame.TeamColor teamColor;

    public LoadGameMessage(ServerMessageType type, Map<ChessPosition, ChessPiece> game, ChessGame.TeamColor teamColor) {
        super(type);
        this.game = game;
        this.teamColor = teamColor;
    }

    public Map<ChessPosition, ChessPiece> getGameData() {
        return game;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
