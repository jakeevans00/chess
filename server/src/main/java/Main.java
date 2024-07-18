import chess.*;
import datastore.DataStore;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        DataStore dataStore = new DataStore();
        server.run(8080);
    }
}