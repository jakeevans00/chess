package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import datastore.DataStore;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import response.LoginResponse;
import response.RegisterResponse;
import service.exceptions.InvalidCredentialsException;
import service.exceptions.MalformedRequestException;

class UserServiceTest {
    private final UserService userService = new UserService();

    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager.deleteAllData();
        userService.register(new UserData("username", "password", "email"));
    }

    @Test
    void loginSuccess() throws Exception {
        UserData user = new UserData("username", "password", "email");
        LoginResponse response = new LoginResponse("username","token");

        Assertions.assertEquals(response.getUsername(), userService.login(user).getUsername());
        Assertions.assertNotNull(response.getAuthToken());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void loginInvalidUsername() {
        UserData user = new UserData("name", "password", "mail");
        Assertions.assertThrows(InvalidCredentialsException.class, () -> userService.login(user));
    }

    @Test
    void loginInvalidPassword() {
        UserData user = new UserData("username", "wrongPassword", "email");
        Assertions.assertThrows(InvalidCredentialsException.class, () -> userService.login(user));
    }

    @Test
    void registerSuccess() throws Exception {
        UserData user = new UserData("newUsername", "password", "email");
        RegisterResponse response = new RegisterResponse("newUsername", "token");

        Assertions.assertEquals(response.getUsername(), userService.register(user).getUsername());
        Assertions.assertNotNull(response.getAuthToken());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void registerInvalidUsername() {
        UserData user = new UserData("", "password", "email");
        Assertions.assertThrows(MalformedRequestException.class, () -> userService.register(user));
    }

    @Test
    void registerInvalidPassword() {
        UserData user = new UserData("badPasswordUser", "", "email");
        Assertions.assertThrows(MalformedRequestException.class, () -> userService.register(user));
    }

    @Test
    void logoutSuccess() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = userService.login(user).getAuthToken();
        Assertions.assertDoesNotThrow(() -> userService.logout(authToken));
    }

    @Test
    void logoutInvalidAuthToken() {
        String invalidAuthToken = "invalidAuthToken";
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(invalidAuthToken));
    }
}