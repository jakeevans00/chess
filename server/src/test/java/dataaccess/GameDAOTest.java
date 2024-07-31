package dataaccess;

import chess.ChessGame;
import model.GameData;
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
    public void testCreateGame() throws SQLException, DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData("testGame", game);

        gameDAO.addGame(gameData);
    }

    @Test
    public void getGameData() throws SQLException, DataAccessException {
        int result = gameDAO.addGame(new GameData("jakesGame", new ChessGame()));
        GameData gameData = gameDAO.getGame(result);
    }

    @Test
    public void getAllGames() throws SQLException, DataAccessException {
        int result = gameDAO.addGame(new GameData("jakesGame", new ChessGame()));
        List<GameData> games = gameDAO.getAllGames();
        Assertions.assertEquals(1, games.size());
    }
}
