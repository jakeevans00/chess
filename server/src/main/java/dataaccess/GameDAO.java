package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.List;

public interface GameDAO {
    GameData getGame(int id) throws SQLException, DataAccessException;
    List<GameData> getAllGames() throws DataAccessException, SQLException;
    int addGame(GameData game) throws DataAccessException, SQLException;
    void updateGame(GameData game) throws SQLException, DataAccessException;
}
