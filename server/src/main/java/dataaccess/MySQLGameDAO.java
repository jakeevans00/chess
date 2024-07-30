package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import handler.Serializer;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public GameData getGame(int id) throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM Game WHERE game_id = ?")) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }

            }
        }
        return null;
    }

    @Override
    public List<GameData> getAllGames() {
        return List.of();
    }

    @Override
    public int addGame(GameData game) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Game (white_username, black_username, name, game) VALUES (?, ?, ?, ?)";
            try (var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());

                var json = Serializer.serialize(game.game());
                ps.setString(4, json);
                ps.executeUpdate();

                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    @Override
    public void updateGame(GameData game) {

    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int game_id = rs.getInt("game_id");
        String white_username = rs.getString("white_username");
        String black_username = rs.getString("black_username");
        String name = rs.getString("name");
        ChessGame json = new Gson().fromJson(rs.getString("game"), ChessGame.class);

        return new GameData(game_id, white_username, black_username, name, json);
    }
}
