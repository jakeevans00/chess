package ui;

import java.util.Scanner;
import ui.EscapeSequences;

public class ConsoleApp {
    Scanner scanner = new Scanner(System.in);
    ServerFacade serverFacade = new ServerFacade();

    public void start() {
        boolean loggedIn = false;
        boolean run = true;

        System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        System.out.printf("\uD83D\uDD25\uD83D\uDD25 Let's play some chess! " +
                "Type 'help' to get started \uD83D\uDD25\uD83D\uDD25%n" +
                "[LOGGED OUT] >>> ");

        while (run) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA);
            String command = scanner.nextLine();
            String[] commandArgs = command.split(" ");

            switch (commandArgs[0]) {
                case "help":
                    System.out.println("some other things");
                    break;
                case "login":
                    loggedIn = true;
                    System.out.println("Now logged in");
                    break;
                case "end":
                    System.out.println("end");
                    scanner.close();
                    run = false;
                    break;
                default:
                    System.out.println("Invalid command");
            }

            if (loggedIn) {
                System.out.printf("%n[LOGGED IN] >> ");
            } else {
                System.out.printf("%n[LOGGED OUT] >> ");
            }
        }
    }
}
