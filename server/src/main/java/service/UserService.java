package service;

import dataaccess.*;
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


    public LoginResponse login(UserData user) throws DataAccessException {
        LoginResponse response = new LoginResponse();

        try {
            UserData result = userDAO.getUser(user.username());

            if (result == null || !user.password().equals(result.password())) {
                response.setMessage("Unauthorized");
                return response;
            }

            AuthData auth = authDAO.createAuth(new AuthData(UUID.randomUUID().toString(), user.username()));
            return new LoginResponse(auth.authToken(), auth.username());
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to access data");
        }
    }

    public void logout(AuthData authData) {}
}
