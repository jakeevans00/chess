package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class UserDAOTest {
    @BeforeEach
    public void setUp() throws Exception {
        try(var conn = DatabaseManager.getConnection()) {
            try(var stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM User");
            }
            try(var stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM Auth");
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void testRegisterUser() {}
}
