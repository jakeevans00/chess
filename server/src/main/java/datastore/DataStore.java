package datastore;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class DataStore {
    private static DataStore instance;
    private HashMap<String, UserData> users;
    private HashMap<String, AuthData> auths;
    private HashMap<Integer, GameData> games;

    private DataStore() {
        UserData test = new UserData("username","password", "email");
        this.users = new HashMap<>();
        this.auths = new HashMap<>();
        this.games = new HashMap<>();

        this.users.put(test.username(),test);
    }

    public static DataStore getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (DataStore.class) {
            if (instance == null) {
                instance = new DataStore();
            }
        }
        return instance;
    }

    public UserData getUser(String username) {
        return this.users.get(username);
    }

    public void addUser(UserData user) {
        this.users.put(user.username(), user);
    }

    public boolean getAuth(String token) {
        return !this.auths.get(token).username().isEmpty();
    }

    public AuthData addAuth(AuthData auth) {
        this.auths.put(auth.username(), auth);
        return auth;
    }

    public void clearAll() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
