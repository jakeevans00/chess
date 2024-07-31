package dataaccess;

import model.GameData;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create tables used by chess application if they don't exist
     */
    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : CREATE_STATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public static int executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
        try (var conn = getConnection()) {
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case GameData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setString(i + 1, null);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException(ex.getMessage());
        }
    }

    public static void deleteAllData() throws SQLException, DataAccessException {
        try (var conn = getConnection()) {
            for (String table : TABLES) {
                String sql = "DELETE FROM " + table;
                try(var ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    private static final String[] TABLES = {
            "Auth", "Game", "User"
    };

    private static final String[] CREATE_STATEMENTS = {
            """
            create table if not exists User (
                user_id  int not null auto_increment,
                username VARCHAR(20)  not null,
                password TEXT         not null,
                email    varchar(255) null,
            constraint User_pk
            primary key (user_id),
            unique key (username),
            index(username)
            );
            """,
            """ 
            create table if not exists Auth (
                auth_id    int not null auto_increment primary key,
                auth_token varchar(36) not null,
                username   varchar(20) not null,
            constraint Auth_User_username_fk
            foreign key (username) references chess.User (username),
            index(auth_token),
            index(username)
            );
            """,
            """
            create table if not exists Game (
                game_id        int auto_increment primary key,
                white_username varchar(20) null,
                black_username varchar(20) null,
                name           varchar(20) not null,
                game           blob        not null,
            constraint Game_User_username_fk
            foreign key (white_username) references chess.User (username),
            constraint Game_User_username_fk_2
            foreign key (black_username) references chess.User (username),
            index(name)
            );
            """,
            """
            create table if not exists Game (
                game_id        int auto_increment primary key,
                white_username varchar(20) null,
                black_username varchar(20) null,
                name           varchar(20) not null,
                game           blob        not null,
            constraint Game_User_username_fk
            foreign key (white_username) references chess.User (username),
            constraint Game_User_username_fk_2
            foreign key (black_username) references chess.User (username),
            index(name)
            );
            """
    };
}




