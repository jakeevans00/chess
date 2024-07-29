package dataaccess;

import model.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {
    @Override
    public void createUser(UserData user) throws Exception {
        String statement = "INSERT INTO User VALUES (?,?,?)";
        DatabaseManager.executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM User WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
            return null;
        }
    }

    private UserData readUser(ResultSet rs) throws Exception {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }
}
