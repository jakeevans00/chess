package datastore;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.*;

public class DataStore {
    private HashMap<String, UserData> users;
    private List<AuthData> authorizations;

    public DataStore() {
        UserData test = new UserData("username","password", "email");
        this.users = new HashMap<>();
        this.users.put(test.username(),test);

        this.authorizations = new ArrayList<>();
    }

    public UserData getUser(String username) {
        return this.users.get(username);
    }

    public void addUser(UserData user) {
        this.users.put(user.username(), user);
    }
}
