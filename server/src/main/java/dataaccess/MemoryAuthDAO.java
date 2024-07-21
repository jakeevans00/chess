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
    public AuthData getAuth(String auth) {
        return DataStore.getInstance().getAuth(auth);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        DataStore.getInstance().deleteAuth(authData.authToken());
    }
}
