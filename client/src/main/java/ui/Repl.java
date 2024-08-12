package ui;

import java.io.PrintStream;
import java.util.*;

import chess.ChessBoard;
import chess.ChessGame;
import client.ChessClient;
import websocket.ServerMessageHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class Repl implements ServerMessageHandler {
    private final Scanner scanner = new Scanner(System.in);
    private final ChessClient client;
    private ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;

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
        styleNotification();
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notificationMessage = (NotificationMessage) notification;
            System.out.println(notificationMessage.getMessage());
        }  else {
            ErrorMessage error = (ErrorMessage) notification;
            System.out.println(error.getErrorMessage());
        }
        reset();
    }

    @Override
    public void updateBoard(LoadGameMessage message) {
        ChessBoard board = new ChessBoard(message.getGameData());
        BoardPrinter printer = new BoardPrinter(board);

        System.out.println();
        printer.drawBoard(this.currentColor);
    }

    private void styleNotification() {
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_RED);
    }

    private void reset() {
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_DARK_GREY);
    }

    public void setTeamColor(ChessGame.TeamColor color) {
        this.currentColor = color;
    }
}