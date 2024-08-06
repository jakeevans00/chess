package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.JoinGameRequest;
import server.response.CreateGameResponse;
import server.response.LoginResponse;
import service.exceptions.ExistingUserException;
import service.exceptions.MalformedRequestException;

import java.sql.SQLException;

public class GameServiceTest {
    GameService gameService = new GameService();
    UserService userService = new UserService();

    @BeforeEach
    void setUp() {
        try {
            DatabaseManager.deleteAllData();
        } catch(SQLException | DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void createGameSuccess() throws Exception {
        GameData gameData = new GameData("My new game!");
        CreateGameResponse response = gameService.createGame(gameData);
        Assertions.assertTrue(response.getGameID() > 0);
    }

    @Test
    public void createDuplicateGame() throws Exception {
        GameData gameData = new GameData("My new game!");
        CreateGameResponse response = gameService.createGame(gameData);
        Assertions.assertTrue(response.getGameID() > 0);
        Assertions.assertThrows(MalformedRequestException.class, () -> gameService.createGame(gameData));
    }

    @Test
    public void createGameBadRequest() {
        GameData gameData = new GameData("");
        Assertions.assertThrows(MalformedRequestException.class, () -> gameService.createGame(gameData));
    }

    @Test void joinGameSuccess() throws Exception {
        userService.register(new UserData("username", "password", "email"));
        userService.register(new UserData("username2", "password2", "email2"));
        LoginResponse loginResponse = userService.login(new UserData("username", "password", "email"));
        LoginResponse loginResponse2 = userService.login(new UserData("username2", "password2", "email2"));

        GameData gameData = new GameData("My new game!");
        int gameId = gameService.createGame(gameData).getGameID();
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameId);
        JoinGameRequest request2 = new JoinGameRequest(ChessGame.TeamColor.BLACK, gameId);

        Assertions.assertDoesNotThrow(() -> gameService.joinGame(request, loginResponse.getAuthToken()));
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(request2, loginResponse2.getAuthToken()));
    }

    @Test void joinGameBadRequest() throws Exception {
        userService.register(new UserData("username", "password", "email"));
        userService.register(new UserData("username2", "password2", "email2"));
        LoginResponse loginResponse = userService.login(new UserData("username", "password", "email"));
        LoginResponse loginResponse2 = userService.login(new UserData("username2", "password2", "email2"));

        GameData gameData = new GameData("My new game!");
        int gameId = gameService.createGame(gameData).getGameID();

        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameId);
        JoinGameRequest request2 = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameId);

        Assertions.assertDoesNotThrow(() -> gameService.joinGame(request, loginResponse.getAuthToken()));
        Assertions.assertThrows(ExistingUserException.class, () -> gameService.joinGame(request2, loginResponse2.getAuthToken()));
    }
}
