package websocket;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import server.utilities.ChessPositionAdapter;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    ChessBoard latestBoard;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter())
            .create();

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler, String authToken) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketUri = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                            latestBoard = new ChessBoard(loadGameMessage.getGameData());
                            serverMessageHandler.updateBoard(gson.fromJson(message, LoadGameMessage.class));
                        }
                        case NOTIFICATION -> serverMessageHandler.notify(gson.fromJson(message, NotificationMessage.class));
                        case ERROR -> serverMessageHandler.notify(gson.fromJson(message, ErrorMessage.class));
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    public void observeGame(String authToken, int gameId) {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException("observe in ws facade" + e.getMessage());
        }
    }

    public void joinGame(String authToken, int gameId) throws IOException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException("in join on web socket facade" + e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws IOException {
        try {
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameId, move);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException("make move in ws facade" + e.getMessage());
        }
    }

    public void resign(String authToken, int gameId) throws IOException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameId);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException("in resign on web socket facade" + e.getMessage());
        }
    }

    public void leave(String authToken, int gameId) throws IOException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameId);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException("in leave on web socket facade" + e.getMessage());
        }
    }

    public ChessBoard getLatestBoard() {
        return this.latestBoard;
    }
}
