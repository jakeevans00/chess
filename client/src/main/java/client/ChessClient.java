package client;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.request.JoinGameRequest;
import server.response.ListGamesResponse;
import server.response.LoginResponse;
import server.response.RegisterResponse;
import server.ServerFacade;
import ui.BoardPrinter;
import ui.EscapeSequences;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChessClient {
    private final ServerFacade server;
    private final Map<Integer, GameData> listedGames = new LinkedHashMap<>();
    private AuthData auth;
    private State state = State.LOGGED_OUT;

    public ChessClient(int port) {
        server = new ServerFacade(port);
    }

    public String eval(String input) {

        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "help" -> displayOptions();
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
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

                RegisterResponse registerResponse = server.register(new UserData(username, password, email));
                auth = new AuthData(registerResponse.getUsername(), registerResponse.getAuthToken());
                state = State.LOGGED_IN;
                return "Successfully registered " + auth.username();
            } catch (ResponseException e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];

            LoginResponse loginResponse = server.login(new UserData(username, password, ""));
            auth = new AuthData(loginResponse.getUsername(), loginResponse.getAuthToken());
            state = State.LOGGED_IN;
            return "Successfully logged in " + auth.username();
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String logout() throws ResponseException {
        assertAuthenticated();
        try {
            server.logout(auth.authToken());
            state = State.LOGGED_OUT;
            auth = null;
            return "Successfully logged out";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        assertAuthenticated();
        if (params.length == 1) {
            try {
                String gameName = params[0];
                server.createGame(auth.authToken(), new GameData(gameName));
                return "Successfully created game " + gameName;
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames() throws ResponseException {
        assertAuthenticated();
        try {
            StringBuilder res = new StringBuilder();
            res.append(String.format("%-10s %-20s %-15s %-15s%n", "Game ID", "Game Name", "White Player", "Black Player"));
            res.append("---------------------------------------------------------\n");

            ListGamesResponse listGamesResponse = server.listGames(auth.authToken());
            for (int i = 1; i < listGamesResponse.getGames().size() + 1; i++) {
                GameData game = listGamesResponse.getGames().get(i - 1);
                listedGames.put(i,game);
                res.append(String.format("%-10d %-20s %-15s %-15s%n",
                        i, game.gameName(), game.whiteUsername(), game.blackUsername()));
            }
            return res.toString();

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String joinGame(String... params) throws ResponseException {
        assertAuthenticated();
        if (params.length == 2) {
            try {
                GameData selected = listedGames.get(Integer.parseInt(params[0]));
                ChessGame.TeamColor color = ChessGame.TeamColor.fromString(params[1]);
                JoinGameRequest request = new JoinGameRequest(color, selected.gameID());
                System.out.println(selected.gameID() + " " + selected.gameName());

                server.joinGame(auth.authToken(), request);
                try (PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8)){
                    BoardPrinter boardPrinter = new BoardPrinter(printStream);
                    boardPrinter.drawBoard();
                }

                return "";

            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <GAME_ID> <PLAYER_COLOR>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertAuthenticated();
        if (params.length == 1) {
            try {
                GameData selected = listedGames.get(Integer.parseInt(params[0]));
                ChessGame game = selected.game();

                try (PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8)){
                    BoardPrinter boardPrinter = new BoardPrinter(printStream);
                    boardPrinter.drawBoard();
                }

                return "";
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <GAME_ID>");
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
        if (state == State.LOGGED_OUT || auth.authToken() == null) {
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
