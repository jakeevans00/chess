package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;
import service.exceptions.InvalidCredentialsException;
import service.exceptions.MalformedRequestException;
import service.exceptions.ExistingUserException;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
    }

    public RegisterResponse register(UserData user) throws Exception {
        validateUser(user);

        return ServiceUtils.execute(() -> {
            userDAO.createUser(user);
            AuthData auth = authDAO.createAuth(new AuthData(user.username(), UUID.randomUUID().toString()));
            return new RegisterResponse(auth.username(), auth.authToken());
        });
    }

    public LoginResponse login(UserData user) throws Exception {
        UserData result = userDAO.getUser(user.username());

        if (result == null || !user.password().equals(result.password())) {
            throw new InvalidCredentialsException("Error: Unauthorized");
        }

        return ServiceUtils.execute(() -> {
            AuthData auth = authDAO.createAuth(new AuthData(user.username(), UUID.randomUUID().toString()));
            return new LoginResponse(auth.username(), auth.authToken());
        });
    }

    public LogoutResponse logout(String authToken) throws Exception {
        return ServiceUtils.execute(() -> {
            authDAO.deleteAuth(authDAO.getAuth(authToken));
            return new LogoutResponse();
        });
    }


    private void validateUser(UserData user) throws Exception {
        UserData result = userDAO.getUser(user.username());

        if (result != null) {
            throw new ExistingUserException("Error: Username already exists");
        }

        if (ServiceUtils.isInvalidString(user.username()) || ServiceUtils.isInvalidString(user.password())) {
            throw new MalformedRequestException("Error: Invalid format for username or password");
        }
    }
}
