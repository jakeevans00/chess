package handler;

import model.UserData;
import server.response.LoginResponse;
import server.utilities.Serializer;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;


public class LoginHandler implements Route {
    private static LoginHandler instance;
    private final UserService userService;

    private LoginHandler() {
        this.userService = new UserService();
    }

    public static LoginHandler getInstance() {
        if (instance == null) {
            instance = new LoginHandler();
        }
        return instance;
    }

    @Override
    public String handle(Request request, Response response) {
        UserData userData = Serializer.deserialize(request, UserData.class);

        try {
            LoginResponse loginResponse = userService.login(userData);
            return Serializer.serialize(loginResponse);
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
