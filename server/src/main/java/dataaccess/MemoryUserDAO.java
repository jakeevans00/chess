package dataaccess;

import datastore.DataStore;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) {
        DataStore.getInstance().addUser(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return DataStore.getInstance().getUser(username);

    }
}
