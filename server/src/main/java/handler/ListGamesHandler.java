package handler;

import response.ListGamesResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    GameService gameService = new GameService();

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            return Serializer.serialize(gameService.listGames());
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
