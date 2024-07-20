package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import response.LoginResponse;
import response.RegisterResponse;
import service.exceptions.MalformedRegistrationException;
import service.exceptions.ExistingUserException;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    public RegisterResponse register(UserData user) throws Exception {
        UserData result = userDAO.getUser(user.username());

        if (result != null) {
            throw new ExistingUserException("Already taken");
        }

        if (isInvalidString(user.username()) || isInvalidString(user.password())) {
            throw new MalformedRegistrationException("Bad Request");
        }

        try {
            userDAO.createUser(user);
            AuthData auth = authDAO.createAuth(new AuthData(UUID.randomUUID().toString(), user.username()));
            return new RegisterResponse(auth.username(), auth.authToken());
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to reach database");
        }
    }



public LoginResponse login(UserData user) throws DataAccessException {
    try {
        UserData result = userDAO.getUser(user.username());
        if (result == null || !user.password().equals(result.password())) {
            return new LoginResponse("Error: Invalid credentials");
        }

        AuthData auth = authDAO.createAuth(new AuthData(user.username(), UUID.randomUUID().toString()));
        return new LoginResponse(auth.username(), auth.authToken());

    } catch (Exception e) {
        throw new DataAccessException("Error: Unable to access data");
    }
}



    public void logout(AuthData authData) throws DataAccessException {
        try {
            authDAO.deleteAuth(authData);
        } catch (Exception e) {
            throw new DataAccessException("Error: Database Error");
        }
    }

    private boolean isInvalidString(String str) {
        return str == null || str.length() > 20 || str.contains(" ") || str.isEmpty();
    }
}
