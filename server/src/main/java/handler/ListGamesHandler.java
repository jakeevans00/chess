package handler;

import response.ListGamesResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private static ListGamesHandler instance;
    private final GameService gameService;

    private ListGamesHandler() {
        gameService = new GameService();
    }

    public static ListGamesHandler getInstance() {
        if (instance == null) {
            instance = new ListGamesHandler();
        }
        return instance;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            return Serializer.serialize(gameService.listGames());
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
