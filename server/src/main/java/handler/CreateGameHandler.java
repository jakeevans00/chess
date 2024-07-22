package handler;

import model.GameData;
import response.CreateGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final GameService gameService = new GameService();

    @Override
    public String handle(Request request, Response response) throws Exception {
        GameData gameData = Serializer.deserialize(request, GameData.class);

        try {
            return Serializer.serialize(gameService.createGame(gameData));
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
