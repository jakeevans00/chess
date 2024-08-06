package handler;


import model.UserData;
import server.response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private static RegisterHandler instance;
    private final UserService userService;

    private RegisterHandler() {
        userService = new UserService();
    }

    public static RegisterHandler getInstance() {
        if (instance == null) {
            instance = new RegisterHandler();
        }
        return instance;
    }

    @Override
    public String handle(Request request, Response response) throws Exception {
        UserData userData = Serializer.deserialize(request, UserData.class);

        try {
            RegisterResponse registerResponse = userService.register(userData);
            return Serializer.serialize(registerResponse);
        } catch (Exception e) {
            return ErrorHandler.handleException(e, response);
        }
    }
}
