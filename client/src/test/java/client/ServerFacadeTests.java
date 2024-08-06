package client;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.sql.SQLException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void beforeEach() throws SQLException, DataAccessException {
        DatabaseManager.deleteAllData();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register(new UserData("username", "password","email"));
        Assertions.assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void registerFailure() throws Exception {
        var authData = facade.register(new UserData("username", "password", "email"));
        Assertions.assertThrows(ResponseException.class, () -> facade.register(new UserData("username", "password", "email")));
    }

    @Test
    void login() throws Exception {
        facade.register(new UserData("username", "password","email"));
        var authData = facade.login(new UserData("username", "password",null));
        Assertions.assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void loginFailure() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> facade.login(new UserData("username", "password", "email")));
    }

    @Test
    void createGame() throws Exception {
        var authData = facade.register(new UserData("username", "password","email"));
        facade.logout(authData.getAuthToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.getAuthToken()));
    }

    @Test
    void createGameFailure() throws Exception {
        var authData = facade.register(new UserData("username", "password", "email"));
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("bad token"));
    }

    @Test
    void joinGame() throws Exception {
        var authData = facade.register(new UserData("username", "password","email"));
        facade.logout(authData.getAuthToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.getAuthToken()));
    }

    @Test
    void joinGameFailure() throws Exception {
        var authData = facade.register(new UserData("username", "password", "email"));
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("bad token"));
    }
    @Test
    void listGame() throws Exception {
        var authData = facade.register(new UserData("username", "password","email"));
        facade.logout(authData.getAuthToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.getAuthToken()));
    }

    @Test
    void listGamesFailure() throws Exception {
        var authData = facade.register(new UserData("username", "password", "email"));
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("bad token"));
    }

}
