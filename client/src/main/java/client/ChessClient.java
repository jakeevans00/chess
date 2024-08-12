package client;

import chess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import server.request.JoinGameRequest;
import server.response.*;
import ui.BoardPrinter;
import ui.EscapeSequences;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ChessClient {
    private final ServerFacade server;
    private WebSocketFacade ws;
    private final ServerMessageHandler serverMessageHandler;
    private final String serverUrl = "http://localhost:8080";

    private final Map<Integer, GameData> listedGames = new LinkedHashMap<>();
    private AuthData auth;
    private int globalGameId;
    private ChessGame.TeamColor playingAs;
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
                case "highlight" -> highlightMoves(params);

                default -> "Invalid command, try typing 'help'";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        return authenticateUser(true, params);
    }

    public String login(String... params) throws ResponseException {
        return authenticateUser(false, params);
    }

    public String logout() throws ResponseException {
        assertLoginState();
        try {
            server.logout(auth.authToken());
            clearLocalData();
            return "Successfully logged out";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        assertLoginState();
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
        assertLoginState();
        try {
            ListGamesResponse listGamesResponse = server.listGames(auth.authToken());
            return formatGameList(listGamesResponse);

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String joinGame(String... params) throws ResponseException {
        assertLoginState();
        if (params.length == 2) {
            try {
                globalGameId = Integer.parseInt(params[0]);
                GameData selected = listedGames.get(globalGameId);
                playingAs = ChessGame.TeamColor.fromString(params[1]);

                createWebSocket();
                JoinGameRequest request = new JoinGameRequest(playingAs, selected.gameID());
                server.joinGame(auth.authToken(), request);
                ws.joinGame(auth.authToken(), selected.gameID());
                state = State.PLAYING;
            }
            catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <GAME_ID> <PLAYER_COLOR>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertLoginState();
        if (params.length == 1) {
            try {
                globalGameId = Integer.parseInt(params[0]);
                int gameId = listedGames.get(globalGameId).gameID();

                createWebSocket();
                ws.observeGame(auth.authToken(), gameId);
                state = State.OBSERVING;

                return "Observing game " + gameId;

            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <GAME_ID>");
    }

    private String highlightMoves(String... params) throws ResponseException {
        assertPlayingOrObserving();
        if (params.length == 1) {
            try {
                String position  = params[0];

                int posRow = Integer.parseInt(String.valueOf(position.charAt(1)));
                int posCol = intFromLetter(position.charAt(0));
                ChessPosition startPosition = new ChessPosition(posRow, posCol);

                ChessBoard board = ws.getLatestBoard();
                BoardPrinter printer = new BoardPrinter(board, endingPositions(startPosition));

                if (state == State.OBSERVING) {
                    printer.drawBoard(ChessGame.TeamColor.WHITE);
                } else {
                    printer.drawBoard(playingAs);
                }
                return "";

            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <FROM>");
    }

    private String resign() throws ResponseException {
        assertPlaying();
        try {
            System.out.println("Are you sure you want to resign? (Y/n");
            if (System.console().readLine().equals("Y")) {
                ws.resign(auth.authToken(), globalGameId);
            } else {
                return "Don't give up, try rook to E4";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Player resigned";
    }

    private String leaveGame() throws ResponseException, IOException {
        assertPlayingOrObserving();
        ws.leave(auth.authToken(), listedGames.get(globalGameId).gameID());
        state = State.LOGGED_IN;
        return "Left the game";
    }

    private String makeMove(String... params) throws ResponseException {
        assertPlaying();

        if (params.length == 2) {
            try {
                String from  = params[0];
                String to = params[1];

                int fromRow = Integer.parseInt(String.valueOf(from.charAt(1)));
                int fromCol = intFromLetter(from.charAt(0));

                int toRow = Integer.parseInt(String.valueOf(to.charAt(1)));
                int toCol = intFromLetter(to.charAt(0));

                ChessMove move = new ChessMove(new ChessPosition(fromRow, fromCol), new ChessPosition(toRow, toCol));
                ws.makeMove(auth.authToken(), listedGames.get(globalGameId).gameID(), move);
                return "made move";

            } catch (Exception e ) {
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <FROM> <TO>");
    }

    private String redraw() throws ResponseException {
        assertPlayingOrObserving();
        ChessBoard board = ws.getLatestBoard();
        BoardPrinter printer = new BoardPrinter(board);

        if (state == State.OBSERVING) {
            printer.drawBoard(ChessGame.TeamColor.WHITE);
        } else {
            printer.drawBoard(playingAs);
        }
        return "";
    }

    private Set<ChessPosition> endingPositions(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = ChessMoveRules.pieceMoves(ws.getLatestBoard(), startPosition);
        Set<ChessPosition> endingPositions = new HashSet<>();
        for (ChessMove validMove : validMoves) {
            endingPositions.add(validMove.getEndPosition());
        }
        return endingPositions;
    }

    private void createWebSocket() throws ResponseException {
        if (ws == null) {
            ws = new WebSocketFacade(serverUrl, serverMessageHandler, auth.authToken());
        }
    }

    private String authenticateUser(boolean isRegistration, String... params) throws ResponseException {
        String operationName = isRegistration ? "registered" : "logged in";

        if (auth != null) {
            throw new ResponseException(400, "You are already logged in, try 'logout'");
        }

        if (params.length >= 2) {
            try {
                String username = params[0];
                String password = params[1];
                String email = isRegistration && params.length > 2 ? params[2] : "";

                AuthResponse response;
                if (isRegistration) {
                    response = server.register(new UserData(username, password, email));
                } else {
                    response = server.login(new UserData(username, password, ""));
                }

                auth = new AuthData(response.getUsername(), response.getAuthToken());
                state = State.LOGGED_IN;
                return "Successfully " + operationName + " " + auth.username();
            } catch (ResponseException e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>" + (isRegistration ? " <EMAIL>" : ""));
    }

    private String formatGameList(ListGamesResponse listGamesResponse) {
        StringBuilder res = new StringBuilder();
        res.append(String.format("%-10s %-20s %-15s %-15s%n", "Game ID", "Game Name", "White Player", "Black Player"));
        res.append("---------------------------------------------------------\n");

        for (int i = 1; i < listGamesResponse.getGames().size() + 1; i++) {
            GameData game = listGamesResponse.getGames().get(i - 1);
            listedGames.put(i,game);
            res.append(String.format("%-10d %-20s %-15s %-15s%n",
                    i, game.gameName(), game.whiteUsername(), game.blackUsername()));
        }
        return res.toString();
    }

    private int intFromLetter(char ch) {
        if (ch < 'a' || ch > 'h') {
            throw new IllegalArgumentException("Invalid character: " + ch);
        }

        return ch - 'a' + 1;
    }

    public void showUser() {
        if (state != State.LOGGED_OUT) {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED IN - " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + auth.username() + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " ] >>> ");
        } else {
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "%n[LOGGED OUT] >>> ");
        }
    }

    private void clearLocalData() {
        state = State.LOGGED_OUT;
        playingAs = null;
        auth = null;
        globalGameId = 0;
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

    private void assertLoginState() throws ResponseException {
        assertAuthenticated();
        if (state == State.PLAYING || state == State.OBSERVING) {
            throw new ResponseException(400, "You can't run this command right now, try 'leave' first");
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
        put("move <FROM> <TO>", " - a piece on your turn. FROM/TO notation ('a1 d6')");
        put("resign", " - from the match");
        put("help", " - with possible commands");
    }};
}
