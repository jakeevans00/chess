package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData);
}
