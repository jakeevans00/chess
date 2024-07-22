package handler;

import model.GameData;
import response.CreateGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    @Override
    public String handle(Request request, Response response) throws Exception {
        GameService gameService = new GameService();
        GameData gameData = Serializer.deserialize(request, GameData.class);

        System.out.println(gameData);

        try {
            return Serializer.serialize(gameService.createGame(gameData));
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
