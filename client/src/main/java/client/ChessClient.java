package client;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import response.LoginResponse;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChessClient {
    private final ServerFacade server;
    private AuthData auth;
    private State state = State.LOGGED_OUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {

        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "help" -> displayOptions();
                case "register"-> register(params);
                case"login" -> login(params);
                case "logout" -> logout();
                case "create" -> create();
                case "list" -> list();
                case "join" -> join();
                case "observe" -> observe();
                case "quit" -> "quit";
                default -> "Invalid command, try typing help";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 2) {
            try {
                String username = params[0];
                String password = params[1];
                String email = params.length > 2 ? params[2] : "";

                LoginResponse loginResponse = server.register(new UserData(username, password, email));
                auth = new AuthData(loginResponse.getUsername(), loginResponse.getAuthToken());
                state = State.LOGGED_IN;
                return "Successfully registered " + auth.username();
            } catch (ResponseException e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) {
        return "";
    }

    public String logout() {
        return "";
    }

    public String create() {
        return "";
    }

    public String list() {
        return "";
    }
    public String join() {
        return "";
    }
    public String observe() {
        return "";
    }
    public String observe(String... params) {
        return "";
    }







    public void showUser() {
        if (state == State.LOGGED_IN) {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED IN - " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + auth.username() + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " ] >>> ");
        } else {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED OUT] >>> ");
        }
    }

    private void assertAuthenticated() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You must be signed in to run this command");
        }
    }

    private String displayOptions() {
        StringBuilder result = new StringBuilder();

        if (state == State.LOGGED_IN) {
            AUTHENTICATED_COMMANDS.forEach((cmd, desc) ->
                    result.append("\t" + EscapeSequences.SET_TEXT_COLOR_BLUE).append(cmd)
                          .append(EscapeSequences.SET_TEXT_COLOR_MAGENTA).append(desc).append("\n"));
        } else {
            UNAUTHENTICATED_COMMANDS.forEach((cmd, desc) ->
                    result.append("\t" + EscapeSequences.SET_TEXT_COLOR_BLUE).append(cmd)
                          .append(EscapeSequences.SET_TEXT_COLOR_MAGENTA).append(desc).append("\n"));
        }

        return result.toString();
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
