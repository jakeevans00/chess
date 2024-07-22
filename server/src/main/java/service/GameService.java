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
import service.exceptions.ExistingUserException;
import service.exceptions.ForbiddenActionException;
import service.exceptions.MalformedRequestException;

import java.util.Objects;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService() {
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
    }

    public CreateGameResponse createGame(GameData gameDataRequest) throws Exception {
        validateCreateGameRequest(gameDataRequest);

        return ServiceUtils.execute(() -> {
            GameData game = new GameData(DataStore.getInstance().getNextCount(), null, null, gameDataRequest.gameName(), new ChessGame());
            gameDAO.addGame(game);
            return new CreateGameResponse(game.gameID());
        });
    }

    public JoinGameResponse joinGame(JoinGameRequest gameDataRequest, String authToken) throws Exception {
        GameData game = DataStore.getInstance().getGame(gameDataRequest.getGameId());
        validateJoinGameRequest(gameDataRequest, game, authToken);

        return ServiceUtils.execute(() -> {
            GameData update = addUserToGame(game, gameDataRequest);
            gameDAO.updateGame(update);
            return new JoinGameResponse();
        });
    }

    public ListGamesResponse listGames() {
        return new ListGamesResponse(gameDAO.getAllGames());
    }

    private GameData addUserToGame(GameData game, JoinGameRequest user) {
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        switch (user.getPlayerColor()) {
            case WHITE:
                whiteUsername = user.getUsername();
                break;
            case BLACK:
                blackUsername = user.getUsername();
                break;
            default:
                if (blackUsername == null) {
                    blackUsername = user.getUsername();
                } else {
                    whiteUsername = user.getUsername();
                }
                break;
        }

        return new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
    }

    private boolean colorIsAvailable(ChessGame.TeamColor playerColor, GameData game) {
        if (Objects.equals(playerColor, ChessGame.TeamColor.WHITE) && game.whiteUsername() != null) {
            return false;
        } else return !Objects.equals(playerColor, ChessGame.TeamColor.BLACK) || game.blackUsername() == null;
    }

    private void validateCreateGameRequest(GameData gameDataRequest) throws MalformedRequestException {
        if (gameDataRequest == null || ServiceUtils.isInvalidString(gameDataRequest.gameName())) {
            throw new MalformedRequestException("Error: Invalid game object or name");
        }

        if (DataStore.getInstance().getGame(gameDataRequest.gameName()) != null) {
            throw new MalformedRequestException("Error: Game already exists");
        }
    }

    private void validateJoinGameRequest(JoinGameRequest joinGameRequest, GameData game, String authToken) throws Exception {
        if (game == null) {
            throw new MalformedRequestException("Error: Game does not exist");
        }

        AuthData auth = authDAO.getAuth(authToken);
        joinGameRequest.setUsername(auth.username());

        if (joinGameRequest.getPlayerColor() == null) {
            throw new MalformedRequestException("Error: Invalid player color");
        }

        if (game.whiteUsername() != null && game.blackUsername() != null) {
            throw new ForbiddenActionException("Error: Game already has 2 players");
        }

        if (!colorIsAvailable(joinGameRequest.getPlayerColor(), game)) {
            throw new ExistingUserException("Error: Cannot play as " + joinGameRequest.getPlayerColor());
        }
    }
}