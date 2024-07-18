package dataaccess;

import datastore.DataStore;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private final DataStore dataStore;

    public MemoryUserDAO() {
        dataStore = new DataStore();
    }

    @Override
    public void createUser(UserData user) {
        dataStore.addUser(user);
    }

    @Override
    public UserData getUser(String username) {
        return dataStore.getUser(username);
    }
}
