package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    AuthData getAuth(UserData user);
    void deleteAuth(AuthData authData);
}
