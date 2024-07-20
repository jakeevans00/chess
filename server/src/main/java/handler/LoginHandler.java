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

    public String handle(Request request, Response response) {
        UserData userData = Serializer.deserialize(request, UserData.class);

        try {
            LoginResponse loginResponse = userService.login(userData);

            if (loginResponse.getMessage() != null) {
                response.status(401);
            }

            return Serializer.serialize(loginResponse);
        } catch (DataAccessException e) {
            return handleDataAccessException(response, e);
        }
    }

    private String handleDataAccessException(Response response, DataAccessException e) {
        response.status(500);
        LoginResponse failedResponse = new LoginResponse();
        failedResponse.setMessage(e.getMessage());
        return Serializer.serialize(failedResponse);
    }
}
