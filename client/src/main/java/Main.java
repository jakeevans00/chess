import server.Server;
import ui.ConsoleApp;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run(8080);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        ConsoleApp consoleManager = new ConsoleApp();
        consoleManager.start();
    }
}