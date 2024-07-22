package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import datastore.DataStore;
import handler.ClearHandler;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearHandlerTest {
    private final DataStore dataStore = DataStore.getInstance();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final UserService userService = new UserService();


    @BeforeEach
    void setUp() throws Exception {
        dataStore.clearAll();
        userService.register(new UserData("username", "password", "email"));
    }

    @Test
    public void clear() throws Exception {
        Assertions.assertNotNull(userDAO.getUser("username"));

        dataStore.clearAll();
        Assertions.assertNull(dataStore.getUser("username"));
    }
}
