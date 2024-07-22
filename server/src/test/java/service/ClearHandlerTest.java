package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import datastore.DataStore;
import handler.ClearHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearHandlerTest {
    @Test
    public void clear() throws Exception {
        DataStore dataStore = DataStore.getInstance();

        UserDAO userDAO = new MemoryUserDAO();
        Assertions.assertNotNull(userDAO.getUser("username"));

        dataStore.clearAll();
        Assertions.assertNull(dataStore.getUser("username"));
    }
}
