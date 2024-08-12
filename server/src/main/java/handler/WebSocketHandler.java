package handler;

import chess.ChessGame;
import chess.ChessGameState;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebSocket
public class WebSocketHandler {
    private Map<Integer, Set<Session>> connections = new HashMap<>();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), session, moveCommand.getMove());
            }
            case LEAVE -> leave();
            case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
        }
    }


    private void connect(String authToken, int gameId, Session session) throws IOException, DataAccessException {
        if (!connections.containsKey(gameId)) {
            connections.put(gameId, new HashSet<>());
        }
        connections.get(gameId).add(session);
        var message = String.format("%s connected", authToken);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        try {
            validateAuth(authToken);
            GameData gameData = getGame(gameId);
            ServerMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, new GameData("test", gameData.game()));

            session.getRemote().sendString(Serializer.serialize(loadGame));
            notifyOthers(gameId, notification, session);

        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(Serializer.serialize(errorMessage));
        }
    }

    private void makeMove(String authToken, int gameId, Session session, ChessMove move) throws IOException, DataAccessException {
        try {
            AuthData authData = getAuth(authToken);
            GameData gameData = getGame(gameId);
            ChessGame game = gameData.game();

            if (!game.validMoves(move.getStartPosition()).contains(move)) {
                throw new RuntimeException("Error, invalid move");
            }

            if (game.getTeamTurn() == ChessGame.TeamColor.WHITE && Objects.equals(gameData.blackUsername(), authData.username()) ||
                game.getTeamTurn() == ChessGame.TeamColor.BLACK && Objects.equals(gameData.whiteUsername(), authData.username())) {
                throw new RuntimeException("Error, can't move on opponent's turn");
            }

            validateNotObserver(authData, gameData);

            if (game.state.getStatus() != ChessGameState.Status.IN_PROGRESS) {
                throw new RuntimeException("Error: Game over, cannot make moves");
            }

            game.makeMove(move);
            GameData update = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(update);
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player made move");
            notifyAll(update);
            notifyOthers(update.gameID(), notification, session);

        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(Serializer.serialize(errorMessage));
        }
    }

    private void leave() {}


    private void resign(String authToken, int gameId, Session session) throws IOException {
        try {
            AuthData authData = getAuth(authToken);
            GameData gameData = getGame(gameId);
            ChessGame game = gameData.game();

            validateNotObserver(authData, gameData);

            if (game.state.getStatus() != ChessGameState.Status.IN_PROGRESS) {
                throw new RuntimeException("Error: Game over, cannot resign");
            }

            game.state.setStatus(ChessGameState.Status.RESIGNED);
            gameDAO.updateGame(gameData);

            NotificationMessage notification =
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, authData.username() + " resigned");
            notifyAll(gameId, notification);

        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(Serializer.serialize(errorMessage));
        }
    }

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

    private void notifyAll(int gameId, NotificationMessage notification) throws IOException {
        Set<Session> sessions = connections.get(gameId);
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getRemote().sendString(Serializer.serialize(notification));
            }
        }
    }

    private void notifyAll(GameData gameData) throws IOException {
        Set<Session> sessions = connections.get(gameData.gameID());
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getRemote().sendString(Serializer.serialize(loadGameMessage));
            }
        }
    }

    private void validateAuth(String authToken) throws ResponseException {
        try {
            if (authDAO.getAuth(authToken) == null) {
                throw new ResponseException(401, "Bad Auth Token");
            }
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Unable to reach database");
        }
    }

    private GameData getGame(int gameId) throws SQLException, DataAccessException, ResponseException {
        if (gameDAO.getGame(gameId) != null) {
            return gameDAO.getGame(gameId);
        }
        throw new ResponseException(402, "Game not found");
    }

    private AuthData getAuth (String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    private void validateNotObserver(AuthData authData, GameData gameData) throws DataAccessException {
        if (!authData.username().equals(gameData.blackUsername()) && !authData.username().equals(gameData.whiteUsername())) {
            throw new RuntimeException("Error, observer cannot make moves");
        }
    }

}
