package dataaccess;

import handler.Serializer;
import model.GameData;

import java.sql.SQLException;
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
    public void addGame(GameData game) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO Game (white_username, black_username, name, game) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());

                var json = Serializer.serialize(game.game());
                System.out.println(json);
                ps.setString(4, json);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void updateGame(GameData game) {

    }
}
