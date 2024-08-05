import server.Server;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        var serverUrl = "http://localhost:8080";

        new Repl(serverUrl).run();
    }
}