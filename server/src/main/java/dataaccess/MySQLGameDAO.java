package dataaccess;

import model.GameData;

import java.util.List;

public class MySQLGameDAO implements GameDAO {
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

    }

    @Override
    public void updateGame(GameData game) {

    }
}
