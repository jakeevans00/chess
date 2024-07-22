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

    @Override
    public void addGame(GameData game) {
        DataStore.getInstance().addGame(game);
    }

    @Override
    public void updateGame(GameData game) {
        DataStore.getInstance().updateGame(game);
    }
}
