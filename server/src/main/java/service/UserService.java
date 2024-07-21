package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;
import service.exceptions.InvalidCredentialsException;
import service.exceptions.MalformedRegistrationException;
import service.exceptions.ExistingUserException;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    public RegisterResponse register(UserData user) throws Exception {
        UserData result = userDAO.getUser(user.username());

        if (result != null) {
            throw new ExistingUserException("Error: Username already exists");
        }

        if (isInvalidString(user.username()) || isInvalidString(user.password())) {
            throw new MalformedRegistrationException("Error: Invalid format for username or password");
        }

        try {
            userDAO.createUser(user);
            AuthData auth = authDAO.createAuth(new AuthData(user.username(), UUID.randomUUID().toString()));
            return new RegisterResponse(auth.username(), auth.authToken());
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to reach database");
        }
    }



    public LoginResponse login(UserData user) throws Exception {
        UserData result = userDAO.getUser(user.username());

        if (result == null || !user.password().equals(result.password())) {
            throw new InvalidCredentialsException("Error: unauthorized");
        }

        try {
            AuthData auth = authDAO.createAuth(new AuthData(user.username(), UUID.randomUUID().toString()));
            return new LoginResponse(auth.username(), auth.authToken());

        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to access data");
        }
    }



    public LogoutResponse logout(String authToken) throws Exception {
        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            System.out.println("Auth was null");
            throw new InvalidCredentialsException("Error: Unauthorized");
        }

        try {
            authDAO.deleteAuth(auth);
            return new LogoutResponse();
        } catch (Exception e) {
            throw new DataAccessException("Error: Database Error");
        }
    }

    private boolean isInvalidString(String str) {
        return str == null || str.length() > 20 || str.contains(" ") || str.isEmpty();
    }
}
