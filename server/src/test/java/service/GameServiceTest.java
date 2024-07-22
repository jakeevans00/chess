package service;

import chess.ChessGame;
import datastore.DataStore;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.JoinGameRequest;
import response.CreateGameResponse;
import response.LoginResponse;
import service.exceptions.ExistingUserException;
import service.exceptions.MalformedRequestException;

import javax.xml.crypto.Data;

public class GameServiceTest {
    GameService gameService = new GameService();
    UserService userService = new UserService();
    DataStore dataStore = DataStore.getInstance();

    @BeforeEach
    void setUp() {
        dataStore.clearAll();
    }

    @Test
    public void createGameSuccess() throws Exception {
        GameData gameData = new GameData("My new game!");
        CreateGameResponse response = gameService.createGame(gameData);
        Assertions.assertEquals(response.getGameID(), 1);
    }

    @Test
    public void createDuplicateGame() throws Exception {
        GameData gameData = new GameData("My new game!");
        CreateGameResponse response = gameService.createGame(gameData);
        Assertions.assertEquals(response.getGameID(), 1);
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
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        JoinGameRequest request2 = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1);

        gameService.createGame(gameData);
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(request, loginResponse.getAuthToken()));
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(request2, loginResponse2.getAuthToken()));
    }

    @Test void joinGameBadRequest() throws Exception {
        userService.register(new UserData("username", "password", "email"));
        userService.register(new UserData("username2", "password2", "email2"));
        LoginResponse loginResponse = userService.login(new UserData("username", "password", "email"));
        LoginResponse loginResponse2 = userService.login(new UserData("username2", "password2", "email2"));

        GameData gameData = new GameData("My new game!");
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        JoinGameRequest request2 = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);

        gameService.createGame(gameData);
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(request, loginResponse.getAuthToken()));
        Assertions.assertThrows(ExistingUserException.class, () -> gameService.joinGame(request2, loginResponse2.getAuthToken()));
    }
}
