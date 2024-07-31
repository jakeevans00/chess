package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

public class GameDAOTest {
    private final GameDAO gameDAO = new MySQLGameDAO();

    @BeforeEach
    public void setUp() throws Exception {
        try {
            DatabaseManager.deleteAllData();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void createGameSuccess() throws SQLException, DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData("Test Game", game);

        Assertions.assertTrue(gameDAO.addGame(gameData) > 0);
    }

    @Test void badCreateGame() throws SQLException, DataAccessException {
        GameData gameData = new GameData(null, null);
        Assertions.assertThrows(Exception.class, () -> {gameDAO.addGame(gameData);});
    }

    @Test
    public void getGameData() throws SQLException, DataAccessException {
        int result = gameDAO.addGame(new GameData("Test Game", new ChessGame()));
        GameData gameData = gameDAO.getGame(result);
        Assertions.assertNotNull(gameData);
    }

    @Test
    public void badGetGameData() throws SQLException, DataAccessException {
        GameData gameData = gameDAO.getGame("bad game");
        Assertions.assertNull(gameData);
    }

    @Test
    public void getAllGames() throws SQLException, DataAccessException {
        gameDAO.addGame(new GameData("Test Game", new ChessGame()));
        gameDAO.addGame(new GameData("another Game", new ChessGame()));
        List<GameData> games = gameDAO.getAllGames();
        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void noneToList() throws SQLException, DataAccessException {
        Assertions.assertEquals(0, gameDAO.getAllGames().size());
    }

    @Test
    public void updateSuccess() throws Exception {
        UserDAO userDAO = new MySQLUserDAO();
        userDAO.createUser(new UserData("white username", "password", ""));
        ChessGame game = new ChessGame();
        GameData gameData = new GameData("TestGame", game);
        int result = gameDAO.addGame(gameData);

        GameData update = new GameData(result,"white username", null, gameData.gameName(), gameData.game());

        Assertions.assertNotEquals(update.whiteUsername(), gameDAO.getGame(update.gameID()).whiteUsername());
        gameDAO.updateGame(update);

        Assertions.assertEquals(update.whiteUsername(), gameDAO.getGame(update.gameID()).whiteUsername());
    }
}
