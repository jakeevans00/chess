package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import datastore.DataStore;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
import response.LoginResponse;
import response.RegisterResponse;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    public RegisterResponse register(UserData user) {throw new RuntimeException(); }


    public LoginResponse login(UserData user) {
        LoginResponse response = new LoginResponse();

        if (userDAO.getUser(user.username()) == null) {
           response.setMessage("User not found");
           return response;
        }

        if (!userDAO.getUser(user.username()).password().equals(user.password())) {
            response.setMessage("Wrong password, try again");
            return response;
        }

        AuthData auth = authDAO.createAuth(new AuthData(UUID.randomUUID().toString(), user.username()));
        return new LoginResponse(auth.authToken(), auth.username());
    }

    public void logout(AuthData authData) {}
}
