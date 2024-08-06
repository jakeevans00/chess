import server.Server;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        var port = server.run(8080);

        new Repl(port).run();
    }
}