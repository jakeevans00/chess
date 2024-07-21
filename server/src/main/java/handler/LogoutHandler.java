package handler;

import model.AuthData;
import response.LogoutResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        UserService userService = new UserService();
        String authToken = request.headers("authorization");

        System.out.println(authToken);

        try {
            LogoutResponse logoutResponse = userService.logout(authToken);
            return Serializer.serialize(logoutResponse);
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
