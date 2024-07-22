package service;

import chess.ChessGame;
import dataaccess.*;
import datastore.DataStore;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import response.CreateGameResponse;
import response.JoinGameResponse;
import response.ListGamesResponse;
import service.exceptions.MalformedRegistrationException;

import java.util.Objects;

public class GameService {
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    public CreateGameResponse createGame(GameData gameDataRequest) throws Exception {
        if (gameDataRequest == null || HelperService.isInvalidString(gameDataRequest.gameName())) {
            throw new MalformedRegistrationException("Error: Invalid game name");
        }

        if (DataStore.getInstance().getGame(gameDataRequest.gameName()) != null) {
            throw new MalformedRegistrationException("Error: Game already exists");
        }

        try {
            ChessGame newGame = new ChessGame();
            GameData game = new GameData(DataStore.getInstance().getNextCount(), null, null, gameDataRequest.gameName(),newGame);
            gameDAO.addGame(game);
            System.out.println(game.gameID());
            return new CreateGameResponse(game.gameID());
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to reach database");
        }
    }

    public JoinGameResponse joinGame(JoinGameRequest gameDataRequest, String authToken) throws Exception {
        System.out.println(gameDataRequest.getGameId());
        GameData game = DataStore.getInstance().getGame(gameDataRequest.getGameId());
        AuthData auth = authDAO.getAuth(authToken);
        gameDataRequest.setUsername(auth.username());

        if (game == null) {
            throw new MalformedRegistrationException("Error: Game does not exist");
        }

        if (game.whiteUsername() != null && game.blackUsername() != null) {
            throw new MalformedRegistrationException("Error: Game already has 2 players");
        }

        if (!colorIsAvailable(gameDataRequest.getPlayerColor(), game)) {
            throw new MalformedRegistrationException("Error: Cannot play as " + gameDataRequest.getPlayerColor());
        }

        try {
            GameData update = addUserToGame(game, gameDataRequest);
            gameDAO.updateGame(update);
            return new JoinGameResponse();
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to reach database");
        }
    }

    public ListGamesResponse listGames() {
        return new ListGamesResponse();
    }

    private GameData addUserToGame(GameData game, JoinGameRequest user) {
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (user.getPlayerColor().equals(ChessGame.TeamColor.WHITE)) {
            whiteUsername = user.getUsername();
        } else if (user.getPlayerColor().equals(ChessGame.TeamColor.BLACK)) {
            blackUsername = user.getUsername();
        } else {
            if (blackUsername == null) {
                blackUsername = user.getUsername();
            } else {
                whiteUsername = user.getUsername();
            }
        }

        return new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
    }

    private boolean colorIsAvailable(ChessGame.TeamColor playerColor, GameData game) {
        if (Objects.equals(playerColor, ChessGame.TeamColor.WHITE) && game.whiteUsername() != null) {
            return false;
        } else return !Objects.equals(playerColor, ChessGame.TeamColor.BLACK) || game.blackUsername() == null;
    }
}
