package handler;


import model.UserData;
import response.RegisterResponse;
import service.UserService;
import service.exceptions.ExistingUserException;
import service.exceptions.MalformedRegistrationException;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    @Override
    public String handle(Request request, Response response) throws Exception {
        UserData userData = Serializer.deserialize(request, UserData.class);
        UserService userService = new UserService();

        try {
            RegisterResponse registerResponse = userService.register(userData);
            return Serializer.serialize(registerResponse);
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
