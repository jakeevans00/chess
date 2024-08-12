package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebSocket
public class WebSocketHandler {
    private Map<Integer, Set<Session>> connections = new HashMap<>();
    private final GameDAO gameDAO = new MySQLGameDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, SQLException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.out.println(error.getMessage());
    }

    private void connect(String authToken, int gameId, Session session) throws IOException, DataAccessException {
        if (!connections.containsKey(gameId)) {
            connections.put(gameId, new HashSet<>());
        }
        connections.get(gameId).add(session);
        var message = String.format("%s connected", authToken);

        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            GameData chessGame = gameDAO.getGame(gameId);
            ChessGame game = chessGame.game();
            ServerMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, new GameData("test", game));

            session.getRemote().sendString(Serializer.serialize(loadGame));
            notifyOthers(gameId, notification, session);

        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(Serializer.serialize(errorMessage));
        }
    };

    private void makeMove() {};
    private void leave() {};
    private void resign() {};

    private void notifyOthers(int gameId, NotificationMessage notification, Session toExclude) throws IOException {
        Set<Session> sessions = connections.get(gameId);
        for (Session session : sessions) {
            if (session.isOpen()) {
                if (session != toExclude) {
                    session.getRemote().sendString(Serializer.serialize(notification));
                }
            } else {
                session.close();
            }
        }
    }

}
