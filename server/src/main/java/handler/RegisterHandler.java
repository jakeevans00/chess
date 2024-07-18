package handler;


import model.UserData;
import response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        UserData userData = Serializer.serialize(request, UserData.class);
        UserService userService = new UserService();
        RegisterResponse registerResponse = userService.register(userData);

        return Serializer.deserialize(registerResponse);
    }
}
