package websocket;

import chess.ChessGame;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage notification);
    void updateBoard(LoadGameMessage loadGameMessage);
    void setTeamColor(ChessGame.TeamColor teamColor);
}