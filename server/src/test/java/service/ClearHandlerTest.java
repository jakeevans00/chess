package service;

import dataaccess.DatabaseManager;
import dataaccess.MySQLUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

public class ClearHandlerTest {
    private final UserDAO userDAO = new MySQLUserDAO();
    private final UserService userService = new UserService();


    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager.deleteAllData();
    }

    @Test
    public void clear() throws Exception {
        userService.register(new UserData("username", "password", "email"));
        Assertions.assertNotNull(userDAO.getUser("username"));

        DatabaseManager.deleteAllData();
        Assertions.assertNull(userDAO.getUser("username"));
    }
}
