package service;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import response.LoginResponse;
import response.RegisterResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void loginSuccess() throws DataAccessException {
        UserService userService = new UserService();
        UserData user = new UserData("username", "password", "email");
        LoginResponse response = new LoginResponse("username","token");

        Assertions.assertEquals(response.getUsername(), userService.login(user).getUsername());
        Assertions.assertNotNull(response.getAuthToken());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void loginInvalidUsername() throws DataAccessException {
        UserService userService = new UserService();
        UserData user = new UserData("name", "password", "mail");
        LoginResponse response = userService.login(user);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertNull(response.getAuthToken());
        Assertions.assertNull(response.getUsername());
    }

    @Test
    void loginInvalidPassword() throws DataAccessException {
        UserService userService = new UserService();
        UserData user = new UserData("username", "wrongPassword", "email");
        LoginResponse response = userService.login(user);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertNull(response.getAuthToken());
        Assertions.assertNull(response.getUsername());
    }

    @Test
    void registerSuccess() throws Exception {
        UserService userService = new UserService();
        UserData user = new UserData("username", "password", "email");
        RegisterResponse response = userService.register(user);
    }
}