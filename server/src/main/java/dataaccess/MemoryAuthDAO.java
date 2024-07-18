package dataaccess;

import datastore.DataStore;
import model.AuthData;
import model.UserData;

public class MemoryAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(AuthData authData) {
        return DataStore.getInstance().addAuth(authData);
    }

    @Override
    public AuthData getAuth(UserData user) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {

    }
}
