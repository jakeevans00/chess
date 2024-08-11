package client;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import server.request.JoinGameRequest;
import server.response.CreateGameResponse;
import server.response.ListGamesResponse;
import server.response.LoginResponse;
import server.response.RegisterResponse;
import ui.BoardPrinter;
import ui.EscapeSequences;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChessClient {
    private final ServerFacade server;
    private WebSocketFacade ws;
    private final ServerMessageHandler serverMessageHandler;
    private final String serverUrl = "http://localhost:8080";

    private final Map<Integer, GameData> listedGames = new LinkedHashMap<>();
    private AuthData auth;
    private State state = State.LOGGED_OUT;

    public ChessClient(int port, ServerMessageHandler serverMessageHandler) {
        server = new ServerFacade(port);
        this.serverMessageHandler = serverMessageHandler;
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
                case "quit" -> "Goodbye!";

                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();

                case "move" -> makeMove(params);
                case "leave" -> leaveGame();
                case "redraw" -> redraw();
                case "resign" -> resign();
                case "highlight" -> highlightMoves();

                default -> "Invalid command, try typing 'help'";
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
                CreateGameResponse createGameResponse = server.createGame(auth.authToken(), new GameData(gameName));
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

                server.joinGame(auth.authToken(), request);
                try {
                    PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);
                    BoardPrinter boardPrinter = new BoardPrinter(printStream, selected.game().getBoard());
                    boardPrinter.drawBoard(color);
                    return "Successfully joined game ";
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
            catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <GAME_ID> <PLAYER_COLOR>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertAuthenticated();
        if (params.length == 1) {
            try {
                ws = new WebSocketFacade(serverUrl, serverMessageHandler);
                GameData selected = listedGames.get(Integer.parseInt(params[0]));
                System.out.println(selected.game().getBoard().toString());

                try {
                    PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);
                    BoardPrinter boardPrinter = new BoardPrinter(printStream, selected.game().getBoard());
                    boardPrinter.drawBoard(ChessGame.TeamColor.WHITE);
                } catch (Exception e) {
                    return e.getMessage();
                }
                return "Observing game: " + params[0];
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <GAME_ID>");
    }

    private String highlightMoves() throws ResponseException {
        assertPlayingOrObserving();
        return "resign";
    }

    private String resign() throws ResponseException {
        assertPlaying();
        return "resign";
    }

    private String redraw() throws ResponseException {
        assertPlayingOrObserving();
        return "redraw";
    }

    private String leaveGame() throws ResponseException {
        assertPlayingOrObserving();
        return "leave";
    }

    private String makeMove(String... params) throws ResponseException {
        assertPlaying();
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
        if (state == State.LOGGED_OUT || auth.authToken() == null) {
            throw new ResponseException(400, "You must be signed in to run this command");
        }
    }

    private void assertPlayingOrObserving() throws ResponseException {
        assertAuthenticated();
        if (state != State.PLAYING && state != State.OBSERVING) {
            throw new ResponseException(400, "You must be playing/observing a game to run this command");
        }
    }

    private void assertPlaying() throws ResponseException {
        assertAuthenticated();
        if (state != State.PLAYING) {
            throw new ResponseException(400, "You must be playing a game to run this command");
        }
    }

    private String displayOptions() {
        Map<String, String> commands = switch (state) {
            case LOGGED_IN -> AUTHENTICATED_COMMANDS;
            case PLAYING -> {
                Map<String, String> combined = new LinkedHashMap<>(OBSERVER_COMMANDS);
                combined.putAll(GAMEPLAY_COMMANDS);
                yield combined;
            }
            case OBSERVING -> OBSERVER_COMMANDS;
            default -> UNAUTHENTICATED_COMMANDS;
        };

        return commands.entrySet().stream()
                .map(e -> "\t" + EscapeSequences.SET_TEXT_COLOR_BLUE + e.getKey() +
                        EscapeSequences.SET_TEXT_COLOR_MAGENTA + e.getValue() + "\n")
                .collect(Collectors.joining());
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

    private static final Map<String, String> OBSERVER_COMMANDS = new LinkedHashMap<>() {{
        put("redraw", " - the chess board");
        put("leave", " - the game");
        put("highlight <PIECE>", " - legal moves. PIECE notation ('a1')");
        put("help", " - with possible commands");
    }};

    private static final Map<String, String> GAMEPLAY_COMMANDS = new LinkedHashMap<>() {{
        put("move <TO> <FROM>", " - a piece on your turn. TO/FROM notation ('a1 d6')");
        put("resign", " - from the match");
        put("help", " - with possible commands");
    }};
}
