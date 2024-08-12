package websocket;

import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage notification);
    void updateBoard(LoadGameMessage loadGameMessage);
}