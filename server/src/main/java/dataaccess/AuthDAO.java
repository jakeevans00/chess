package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public interface AuthDAO {
    AuthData createAuth(AuthData authData) throws SQLException, DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData authData) throws SQLException, DataAccessException;
}
