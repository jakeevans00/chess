package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws Exception;
    UserData getUser(String username) throws Exception;
}
