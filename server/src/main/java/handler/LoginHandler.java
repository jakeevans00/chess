package handler;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import response.LoginResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;


public class LoginHandler implements Route {
    public String handle(Request request, Response response) {
        UserData userData = Serializer.serialize(request, UserData.class);

        UserService userService = new UserService();
        try {
            LoginResponse loginResponse = userService.login(userData);
            if (loginResponse.getToken() == null) {
                response.status(401);
            }

            return Serializer.deserialize(loginResponse);

        } catch (DataAccessException e) {
            response.status(500);
            return Serializer.deserialize(e.getMessage());
        }
    }
}
