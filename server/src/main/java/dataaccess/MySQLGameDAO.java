package dataaccess;

import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handler.Serializer;
import model.GameData;
import utilities.ChessPositionAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    public GameData getGame(String name) throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM Game WHERE name = ?")) {
                ps.setString(1, name);
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
    public List<GameData> getAllGames() throws DataAccessException, SQLException {
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM Game")) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData game = readGame(rs);
                        games.add(game);
                    }
                }
            }
        }
        return games;
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
            throw new SQLException(e.getMessage());
        }
        return -1;
    }

    @Override
    public void updateGame(GameData gameData) throws SQLException, DataAccessException {
        GameData gameToUpdate = getGame(gameData.gameID());

        if (gameToUpdate != null) {
            String stmt = "UPDATE Game SET white_username = ?, black_username = ?, name = ?, game = ? WHERE game_id = ?";
            var json = Serializer.serialize(gameData.game());
            DatabaseManager.executeUpdate(stmt, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), json, gameToUpdate.gameID());
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameId = rs.getInt("game_id");
        String whiteUsername = rs.getString("white_username");
        String blackUsername = rs.getString("black_username");
        String name = rs.getString("name");
        String game = rs.getString("game");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter())
                .create();
        ChessGame g = gson.fromJson(game, ChessGame.class);

        return new GameData(gameId, whiteUsername, blackUsername, name, g);
    }
}
