package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(AuthData authData) throws SQLException, DataAccessException {
        String statement = "INSERT INTO Auth (auth_token, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, authData.authToken(), authData.username());
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM Auth WHERE auth_token = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not reach database");
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws SQLException, DataAccessException {
        String statement = "DELETE FROM Auth WHERE auth_token = ?";
        DatabaseManager.executeUpdate(statement, authData.authToken());
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("auth_token");
        String username = rs.getString("username");
        return new AuthData(username, authToken);
    }
}
