package handler;

import chess.*;
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
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final Map<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = Serializer.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), session, moveCommand.getMove());
            }
            case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
            case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
        }
    }


    private void connect(String authToken, int gameId, Session session) throws IOException, DataAccessException {
        if (!connections.containsKey(gameId)) {
            connections.put(gameId, new HashSet<>());
        }
        connections.get(gameId).add(session);

        try {
            AuthData authData = authDAO.getAuth(authToken);
            var message = String.format("%s connected", authData.username());
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

            validateAuth(authToken);
            GameData gameData = getGame(gameId);
            LoadGameMessage loadGame =
                    new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                                        gameData.game().getBoard().getChessPieces(),
                                        getColor(authDAO.getAuth(authToken), gameData));

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

            validateMove(game, move, authData, gameData);

            game.makeMove(move);
            GameData updatedGameData = updateGameData(gameData, game);

            String message = authData.username() + " made move " + formatMove(move);
            notifyPlayers(updatedGameData, session, message, authData);
            notifyStatusChange(updatedGameData, session);

        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            System.out.println(e.getMessage());
            session.getRemote().sendString(Serializer.serialize(errorMessage));
        }
    }

    private void leave(String authToken, int gameId, Session session) throws IOException, DataAccessException {
        try {
            GameData gameData = getGame(gameId);
            AuthData authData = getAuth(authToken);
            GameData update = getGameData(gameId, gameData, authData);

            gameDAO.updateGame(update);
            removeSession(gameId, session);
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                                              authData.username() + " left the game");
            notifyOthers(gameId, notification, session);

        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(Serializer.serialize(errorMessage));
        }
    }

    private void resign(String authToken, int gameId, Session session) throws IOException {
        try {
            AuthData authData = getAuth(authToken);
            GameData gameData = getGame(gameId);
            ChessGame game = gameData.game();

            validateNotObserver(authData, gameData);

            if (game.state.getStatus() == ChessGameState.Status.CHECKMATE ||
                game.state.getStatus() == ChessGameState.Status.STALEMATE ||
                game.state.getStatus() == ChessGameState.Status.RESIGNED) {
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

    private void validateMove(ChessGame game, ChessMove move, AuthData authData, GameData gameData) {
        if (game.state.getStatus() == ChessGameState.Status.CHECKMATE ||
            game.state.getStatus() == ChessGameState.Status.STALEMATE ||
            game.state.getStatus() == ChessGameState.Status.RESIGNED) {
            throw new RuntimeException("Game over, cannot make moves");
        }

        if (!game.validMoves(move.getStartPosition()).contains(move)) {
            throw new RuntimeException("Invalid move");
        }

        if (isWrongTurn(game, authData, gameData)) {
            throw new RuntimeException("Can't move on opponent's turn");
        }

        validateNotObserver(authData, gameData);
    }

    private boolean isWrongTurn(ChessGame game, AuthData authData, GameData gameData) {
        return (game.getTeamTurn() == ChessGame.TeamColor.WHITE && Objects.equals(gameData.blackUsername(), authData.username())) ||
                (game.getTeamTurn() == ChessGame.TeamColor.BLACK && Objects.equals(gameData.whiteUsername(), authData.username()));
    }

    private GameData updateGameData(GameData gameData, ChessGame game) throws DataAccessException, SQLException {
        GameData updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(updatedGameData);
        return updatedGameData;
    }

    private void notifyPlayers(GameData updatedGameData, Session session, String message, AuthData authData) throws IOException, DataAccessException {
        Map<ChessPosition, ChessPiece> updatedBoard = updatedGameData.game().getBoard().getChessPieces();
        notifyAll(updatedBoard, updatedGameData.gameID(), getColor(authData, updatedGameData));

        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        notifyOthers(updatedGameData.gameID(), notification, session);
    }

    private void notifyStatusChange(GameData gameData, Session session) throws IOException, DataAccessException {
        ChessGameState.Status status = gameData.game().state.getStatus();
        if (status == ChessGameState.Status.CHECKMATE || status == ChessGameState.Status.STALEMATE) {
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Game over by " + status.toString());
            notifyAll(gameData.gameID(), notification);
        }

        if (status == ChessGameState.Status.CHECK) {
            NotificationMessage notification =
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            gameData.game().state.getTurn().toString() + " in " + status.toString());
            notifyAll(gameData.gameID(), notification);
        }
    }

    private void removeSession(int gameId, Session session) {
        Set<Session> sessions = connections.get(gameId);
        sessions.remove(session);
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

        cleanupConnections();
    }

    private void notifyAll(int gameId, NotificationMessage notification) {
        Set<Session> sessions = connections.get(gameId);
        if (sessions == null) { return; }

        Iterator<Session> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            try {
                if (session.isOpen()) {
                    session.getRemote().sendString(Serializer.serialize(notification));
                } else {
                    iterator.remove();
                }
            } catch (IOException | IllegalStateException e) {
                iterator.remove();
                throw new RuntimeException("Error sending message to session: " + e.getMessage());
            }
        }
        cleanupConnections();
    }

    private void notifyAll(Map<ChessPosition, ChessPiece> updatedBoard, int gameId, ChessGame.TeamColor color) throws IOException {
        Set<Session> sessions = connections.get(gameId);
        LoadGameMessage loadGameMessage =
                new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, updatedBoard, color);
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getRemote().sendString(Serializer.serialize(loadGameMessage));
            }
        }
        cleanupConnections();
    }

    public void cleanupConnections() {
        for (Map.Entry<Integer, Set<Session>> entry : connections.entrySet()) {
            entry.getValue().removeIf(session -> !session.isOpen());
        }

        connections.entrySet().removeIf(entry -> entry.getValue().isEmpty());
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

    private void validateNotObserver(AuthData authData, GameData gameData) {
        if (!authData.username().equals(gameData.blackUsername()) && !authData.username().equals(gameData.whiteUsername())) {
            throw new RuntimeException("Error, observer cannot make moves");
        }
    }

    private static GameData getGameData(int gameId, GameData gameData, AuthData authData) {
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String authUsername = authData.username();

        if (blackUsername != null && blackUsername.equals(authUsername)) {
            return new GameData(gameId, whiteUsername, null, gameData.gameName(), gameData.game());
        }

        if (whiteUsername != null && whiteUsername.equals(authUsername)) {
            return new GameData(gameId, null, blackUsername, gameData.gameName(), gameData.game());
        }

        return gameData;
    }

    private String formatMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        String startString = formatPosition(start);
        String endString = formatPosition(end);

        return startString + " to " + endString;
    }

    private String formatPosition(ChessPosition position) {
        char column = (char) ('a' + position.getColumn() - 1);
        int row = position.getRow();
        return String.format("%c%d", column, row);
    }

    private ChessGame.TeamColor getColor(AuthData authData, GameData gameData) {
        String blackUsername = gameData.blackUsername();
        String authUsername = authData.username();

        if (blackUsername != null && blackUsername.equals(authUsername)) {
            return ChessGame.TeamColor.BLACK;
        }

        return ChessGame.TeamColor.WHITE;
    }

}
