
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var port = 8080;
        new Repl(port).run();
    }
}