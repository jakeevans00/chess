package service;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import response.LoginResponse;
import response.RegisterResponse;
import service.exceptions.InvalidCredentialsException;
import service.exceptions.MalformedRegistrationException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void loginSuccess() throws Exception {
        UserService userService = new UserService();
        UserData user = new UserData("username", "password", "email");
        LoginResponse response = new LoginResponse("username","token");

        Assertions.assertEquals(response.getUsername(), userService.login(user).getUsername());
        Assertions.assertNotNull(response.getAuthToken());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void loginInvalidUsername() {
        UserService userService = new UserService();
        UserData user = new UserData("name", "password", "mail");

        Assertions.assertThrows(InvalidCredentialsException.class, () -> userService.login(user));
    }

    @Test
    void loginInvalidPassword() {
        UserService userService = new UserService();
        UserData user = new UserData("username", "wrongPassword", "email");

        Assertions.assertThrows(InvalidCredentialsException.class, () -> userService.login(user));
    }

    @Test
    void registerSuccess() throws Exception {
        UserService userService = new UserService();
        UserData user = new UserData("newUsername", "password", "email");
        RegisterResponse response = new RegisterResponse("newUsername", "token");

        Assertions.assertEquals(response.getUsername(), userService.register(user).getUsername());
        Assertions.assertNotNull(response.getAuthToken());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void registerInvalidUsername() {
        UserService userService = new UserService();
        UserData user = new UserData("", "password", "email");

        Assertions.assertThrows(MalformedRegistrationException.class, () -> userService.register(user));
    }

    @Test
    void registerInvalidPassword() {
        UserService userService = new UserService();
        UserData user = new UserData("newUsername", "pass with spaces", "email");

        Assertions.assertThrows(MalformedRegistrationException.class, () -> userService.register(user));
    }
}