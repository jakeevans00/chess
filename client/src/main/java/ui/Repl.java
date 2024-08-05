package ui;

import java.util.*;

import client.ChessClient;
import model.AuthData;

public class Repl {
    private final Scanner scanner = new Scanner(System.in);
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        introduceGame();
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.printf(result);
                client.showUser();
            } catch (Throwable e) {

                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void introduceGame() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        System.out.printf("\uD83D\uDD25\uD83D\uDD25 Let's play some chess! " +
                "Type 'help' to get started \uD83D\uDD25\uD83D\uDD25%n" +
                "[LOGGED OUT] >>> ");
    }
}