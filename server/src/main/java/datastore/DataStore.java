package datastore;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class DataStore {
    private static DataStore instance;
    private final HashMap<String, UserData> users;
    private final HashMap<String, AuthData> auths;
    private final HashMap<Integer, GameData> games;
    private int counter = 1;

    private DataStore() {
        UserData testUser = new UserData("username","password", "email");
        AuthData testAuth = new AuthData("username", "ddd256dd-b041-4513-910f-c5408a8e6746");
        this.users = new HashMap<>();
        this.auths = new HashMap<>();
        this.games = new HashMap<>();

        this.users.put(testUser.username(), testUser);
        this.auths.put(testAuth.authToken(), testAuth);
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

    public int getNextCount() {
        return counter++;
    }

    public UserData getUser(String username) {
        return this.users.get(username);
    }

    public void addUser(UserData user) {
        this.users.put(user.username(), user);
    }

    public AuthData getAuth(String token) {
        return auths.get(token);
    }

    public AuthData addAuth(AuthData auth) {
        this.auths.put(auth.authToken(), auth);
        return auth;
    }

    public void deleteAuth(String authToken) {
        this.auths.remove(authToken);
    }

    public GameData getGame(int gameId) {
        return this.games.get(gameId);
    }

    public GameData getGame(String gameName) {
        for (GameData game : this.games.values()) {
            if (game.gameName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    public void addGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public List<GameData> getAllGames() {
        return new ArrayList<>(this.games.values());
    }

    public void updateGame(GameData game) {
        this.games.put(game.gameID(), game);
    }

    public void clearAll() {
        users.clear();
        auths.clear();
        games.clear();
        counter = 1;
    }
}
