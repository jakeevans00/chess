package dataaccess;

import datastore.DataStore;
import model.GameData;

import java.sql.SQLException;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    @Override
    public GameData getGame(int id) {
        return DataStore.getInstance().getGame(id);
    }

    @Override
    public GameData getGame(String name) throws SQLException, DataAccessException {
        return null;
    }

    @Override
    public List<GameData> getAllGames() {
        return DataStore.getInstance().getAllGames();
    }

    @Override
    public int addGame(GameData game) {
        DataStore.getInstance().addGame(game);
        return DataStore.getInstance().getNextCount() - 1;
    }

    @Override
    public void updateGame(GameData game) {
        DataStore.getInstance().updateGame(game);
    }
}
