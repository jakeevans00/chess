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
    public AuthData getAuth(String authToken) {
        return DataStore.getInstance().getAuth(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        DataStore.getInstance().deleteAuth(authData.authToken());
    }
}
