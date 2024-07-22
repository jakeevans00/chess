package dataaccess;

import datastore.DataStore;
import model.GameData;

import java.util.List;

public class MemoryGameDAO implements GameDAO {
    @Override
    public GameData getGame(int id) {
        return null;
    }

    @Override
    public List<GameData> getAllGames() {
        return List.of();
    }

    public void addGame(GameData game) {
        DataStore.getInstance().addGame(game);
    }
}
