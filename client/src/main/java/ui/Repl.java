package ui;

import java.util.*;

import client.ChessClient;
import com.google.gson.Gson;
import handler.Serializer;
import websocket.ServerMessageHandler;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class Repl implements ServerMessageHandler {
    private final Scanner scanner = new Scanner(System.in);
    private final ChessClient client;


    public Repl(int port) {
        client = new ChessClient(port, this);
    }

    public void run() {
        introduceGame();
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while (!result.equals("Goodbye!")) {
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.printf(result);
                System.out.println();
                if (!line.equals("quit")) {
                    client.showUser();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }

        System.exit(0);
    }

    private void introduceGame() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        System.out.printf("\uD83D\uDD25\uD83D\uDD25 Let's play some chess! " +
                "Type 'help' to get started \uD83D\uDD25\uD83D\uDD25%n" +
                "[LOGGED OUT] >>> ");
    }

    @Override
    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.println("in notify");
        } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            System.out.println("game");
        } else {
            System.out.println("nothing much");
        }
    }
}