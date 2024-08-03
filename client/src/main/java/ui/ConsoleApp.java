package ui;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import ui.EscapeSequences;

import static javax.swing.UIManager.put;

public class ConsoleApp {
    Scanner scanner = new Scanner(System.in);
    ServerFacade serverFacade = new ServerFacade();
    boolean loggedIn = false;

    public void start() {
        boolean run = true;

        System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        System.out.printf("\uD83D\uDD25\uD83D\uDD25 Let's play some chess! " +
                "Type 'help' to get started \uD83D\uDD25\uD83D\uDD25%n" +
                "[LOGGED OUT] >>> ");

        while (run) {
            String command = scanner.nextLine();
            String[] commandArgs = command.split(" ");

            switch (commandArgs[0]) {
                case "help":
                    displayOptions();
                    break;
                case "login":
                    serverFacade.login(commandArgs[1], commandArgs[2]);
                    break;
                case "end":
                    System.out.println("end");
                    scanner.close();
                    run = false;
                    break;
                default:
                    System.out.println("Invalid command");
            }

            showUser();
        }
    }

    private void showUser() {
        if (loggedIn) {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED IN]");
        } else {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED OUT]");
        }
    }

    private void displayOptions() {
        if (loggedIn) {
            System.out.printf("%n[LOGGED IN] >> ");
        } else {
            UNAUTHENTICATED_COMMANDS.forEach((cmd, desc) ->
                    System.out.printf("\t" + EscapeSequences.SET_TEXT_COLOR_BLUE + cmd + EscapeSequences.SET_TEXT_COLOR_MAGENTA + desc + "\n"));
        }
    }

    private static final Map<String, String> UNAUTHENTICATED_COMMANDS = new LinkedHashMap<String, String>() {{
        put("register <USERNAME> <PASSWORD> <EMAIL>", " - to create an account");
        put("login <USERNAME> <PASSWORD>", " - to login");
    }};
}