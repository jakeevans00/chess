package handler;

import model.AuthData;
import response.LogoutResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private static LogoutHandler instance;
    private final UserService userService;

    private LogoutHandler() {
        this.userService = new UserService();
    }

    public static synchronized LogoutHandler getInstance() {
        if (instance == null) {
            instance = new LogoutHandler();
        }
        return instance;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String authToken = request.headers("authorization");

        try {
            LogoutResponse logoutResponse = userService.logout(authToken);
            return Serializer.serialize(logoutResponse);
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
