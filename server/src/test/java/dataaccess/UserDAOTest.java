package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.spec.ECField;
import java.sql.SQLException;

public class UserDAOTest {
    private final UserDAO userDAO = new MySQLUserDAO();

    @BeforeEach
    public void setUp() throws Exception {
        try {
            DatabaseManager.deleteAllData();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void successCreate() throws Exception {
        UserData user = new UserData("username", "pas", "email");
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(user));
    }

    @Test
    public void invalidCreate() throws Exception {
        UserData user = new UserData(null, "pass", "email");
        Assertions.assertThrows(Exception.class, () -> userDAO.createUser(user));
    }

    @Test
    public void successGetUser() throws Exception {
        Assertions.assertNull(userDAO.getUser("user"));
        userDAO.createUser(new UserData("user", "pass", "email"));
        Assertions.assertNotNull(userDAO.getUser("user"));

    }

    @Test
    public void getInvalidUser() throws Exception {
        Assertions.assertNull(userDAO.getUser("user"));
    }

}
