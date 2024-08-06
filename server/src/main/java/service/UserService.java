package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.response.LoginResponse;
import server.response.LogoutResponse;
import server.response.RegisterResponse;
import service.exceptions.InvalidCredentialsException;
import service.exceptions.MalformedRequestException;
import service.exceptions.ExistingUserException;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        this.userDAO = new MySQLUserDAO();
        this.authDAO = new MySQLAuthDAO();
    }

    public RegisterResponse register(UserData user) throws Exception {
        validateUser(user);
        UserData newUser = hashUserData(user);

        return ServiceUtils.execute(() -> {
            userDAO.createUser(newUser);
            AuthData auth = authDAO.createAuth(new AuthData(newUser.username(), UUID.randomUUID().toString()));
            return new RegisterResponse(auth.username(), auth.authToken());
        });
    }

    public LoginResponse login(UserData user) throws Exception {
        UserData result = userDAO.getUser(user.username());

        if (result == null || !BCrypt.checkpw(user.password(), result.password())) {
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
        if (ServiceUtils.isInvalidString(user.username()) || ServiceUtils.isInvalidString(user.password())) {
            throw new MalformedRequestException("Error: Invalid format for username or password");
        }

        UserData result = userDAO.getUser(user.username());

        if (result != null) {
            throw new ExistingUserException("Error: Username already exists");
        }
    }

    private UserData hashUserData(UserData user) {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        return new UserData(user.username(), hashedPassword, user.email());
    }
}
