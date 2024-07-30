package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GameDAOTest {
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
        GameDAO gameDAO = new MySQLGameDAO();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData("testGame", game);

        gameDAO.addGame(gameData);
    }
}
