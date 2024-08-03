package ui;

import java.util.*;

import ui.EscapeSequences;

import static javax.swing.UIManager.put;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade serverFacade = new ServerFacade();
    private boolean loggedIn = false;


    public void start() {
        introduceGame();
        play();
    }

    private void play() {
        while (true) {
            String command = scanner.nextLine();
            String[] commandArgs = command.split(" ");

            switch (commandArgs[0]) {
                case "help" -> displayOptions();
                case "register" -> serverFacade.register(commandArgs);
                case "login" -> loggedIn = serverFacade.login(commandArgs);
                case "logout" -> loggedIn = serverFacade.logout();
                case "quit" -> {
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid command, try typing 'help'");
            }

            showUser();
        }
    }

    private void introduceGame() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        System.out.printf("\uD83D\uDD25\uD83D\uDD25 Let's play some chess! " +
                "Type 'help' to get started \uD83D\uDD25\uD83D\uDD25%n" +
                "[LOGGED OUT] >>> ");
    }

    private void showUser() {
        if (loggedIn) {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED IN] >>> ");
        } else {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED OUT] >>> ");
        }
    }

    private void displayOptions() {
        if (loggedIn) {
            AUTHENTICATED_COMMANDS.forEach((cmd, desc) ->
                    System.out.printf("\t" + EscapeSequences.SET_TEXT_COLOR_BLUE + cmd + EscapeSequences.SET_TEXT_COLOR_MAGENTA + desc + "\n"));
        } else {
            UNAUTHENTICATED_COMMANDS.forEach((cmd, desc) ->
                    System.out.printf("\t" + EscapeSequences.SET_TEXT_COLOR_BLUE + cmd + EscapeSequences.SET_TEXT_COLOR_MAGENTA + desc + "\n"));
        }
    }

    private static final Map<String, String> UNAUTHENTICATED_COMMANDS = new LinkedHashMap<>() {{
        put("register <USERNAME> <PASSWORD> <EMAIL>", " - to create an account");
        put("login <USERNAME> <PASSWORD>", " - to login");
        put("quit", " - if you're a quitter");
        put("help", " - with possible commands");
    }};

    private static final Map<String, String> AUTHENTICATED_COMMANDS = new LinkedHashMap<>() {{
        put("create <GAME>", " - a game");
        put("list", " - games");
        put("join <ID> [WHITE|BLACK]", " - a game");
        put("observe <ID>", " - a game");
        put("quit", " - playing chess");
        put("help", " - with possible commands");
    }};
}