package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class AuthDAOTest {
    private final AuthDAO authDAO = new MySQLAuthDAO();
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
        userDAO.createUser(new UserData("username", "password", ""));
        AuthData authData = new AuthData("username", "password");
        Assertions.assertEquals(authData.authToken(), authDAO.createAuth(authData).authToken());
    }

    @Test
    public void failCreate() throws Exception {
        userDAO.createUser(new UserData("username", "password", ""));
        AuthData authData = new AuthData("username", null);
        Assertions.assertThrows(Exception.class, () -> authDAO.createAuth(authData));
    }

    @Test
    public void successGet() throws Exception {
        userDAO.createUser(new UserData("username", "password", ""));
        AuthData authData = new AuthData("username", "password");
        AuthData result = authDAO.createAuth(authData);

        Assertions.assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void failGet() throws Exception {
        Assertions.assertNull(authDAO.getAuth("bogus"));
    }

    @Test
    public void successDelete() throws Exception {
        userDAO.createUser(new UserData("username", "password", ""));
        AuthData authData = new AuthData("username", "password");
        AuthData result = authDAO.createAuth(authData);
        Assertions.assertNotNull(authDAO.getAuth(result.authToken()));
        authDAO.deleteAuth(authData);
        Assertions.assertNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void failDelete() throws Exception {
        Assertions.assertNull(authDAO.getAuth("fake"));
    }
}
