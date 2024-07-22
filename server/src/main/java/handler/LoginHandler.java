package handler;

import dataaccess.DataAccessException;
import model.UserData;
import response.LoginResponse;
import service.UserService;
import service.exceptions.MalformedRegistrationException;
import spark.Request;
import spark.Response;
import spark.Route;


public class LoginHandler implements Route {
    private final UserService userService = new UserService();

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
