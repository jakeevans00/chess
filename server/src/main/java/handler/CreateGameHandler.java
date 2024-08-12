package handler;

import model.GameData;
import server.utilities.Serializer;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private static CreateGameHandler createGameHandler;
    private final GameService gameService;

    private CreateGameHandler() {
        this.gameService = new GameService();
    }

    public static CreateGameHandler getInstance() {
        if (createGameHandler == null) {
            createGameHandler = new CreateGameHandler();
        }
        return createGameHandler;
    }

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
