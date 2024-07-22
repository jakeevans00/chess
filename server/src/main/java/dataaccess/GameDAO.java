package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    GameData getGame(int id);
    List<GameData> getAllGames();
    void addGame(GameData game);
}
