package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import dataaccess.MySQLGameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.request.JoinGameRequest;
import server.Server;
import server.ServerFacade;
import server.response.AuthResponse;
import server.response.CreateGameResponse;
import server.response.RegisterResponse;

import java.sql.SQLException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    AuthResponse authData;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void beforeEach() throws SQLException, DataAccessException, ResponseException {
        DatabaseManager.deleteAllData();
        authData = new AuthResponse();
        this.authData = facade.register(new UserData("username", "password","email"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() {
        Assertions.assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void registerFailure() {
        Assertions.assertThrows(ResponseException.class, () -> facade.register(new UserData("username", "password", "email")));
    }

    @Test
    void login() throws Exception {
        var authData = facade.login(new UserData("username", "password",null));
        Assertions.assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void loginFailure() {
        Assertions.assertThrows(ResponseException.class, () -> facade.login(new UserData("user", "password", "email")));
    }

    @Test
    void logout() throws Exception {
        var authData = facade.login(new UserData("username", "password",null));
        Assertions.assertDoesNotThrow((()->facade.logout(authData.getAuthToken())));
    }

    @Test
    void logoutFailure() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("dummy data"));
    }

    @Test
    void createGame() throws Exception {
        Assertions.assertDoesNotThrow(() -> facade.createGame(authData.getAuthToken(), new GameData("new game")));
    }

    @Test
    void createGameFailure() throws Exception {
        facade.createGame(authData.getAuthToken(), new GameData("new game"));
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(authData.getAuthToken(), new GameData("new game")));
    }

    @Test
    void joinGame() throws Exception {
        CreateGameResponse createGameResponse = facade.createGame(authData.getAuthToken(), new GameData("new game"));
        Assertions.assertDoesNotThrow(() -> facade.joinGame(authData.getAuthToken(),
                new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResponse.getGameID())));
    }

    @Test
    void joinGameFailure() throws Exception {
        facade.createGame(authData.getAuthToken(), new GameData("new game"));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(authData.getAuthToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, 1)));
    }

    @Test
    void listGame() throws Exception {
        facade.createGame(authData.getAuthToken(), new GameData("new game"));
        facade.createGame(authData.getAuthToken(), new GameData("new game 2"));
        facade.listGames(authData.getAuthToken());
        Assertions.assertDoesNotThrow(() -> facade.listGames(authData.getAuthToken()));
    }

    @Test
    void listGamesFailure() throws Exception {
        facade.createGame(authData.getAuthToken(), new GameData("new game"));
        facade.createGame(authData.getAuthToken(), new GameData("new game 2"));
        facade.listGames(authData.getAuthToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("bad token"));
    }

}
