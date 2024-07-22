package handler;

import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final GameService gameService = new GameService();

    @Override
    public Object handle(Request request, Response response) throws Exception {
        System.out.println(Serializer.deserialize(request, JoinGameRequest.class).getGameId());
        JoinGameRequest gameData = Serializer.deserialize(request, JoinGameRequest.class);
        String authToken = request.headers("Authorization");

        try {
            return Serializer.serialize(gameService.joinGame(gameData, authToken));
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
