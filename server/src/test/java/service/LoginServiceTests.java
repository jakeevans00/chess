//package service;
//
//import org.junit.jupiter.api.*;
//import passoff.model.TestAuthResult;
//import passoff.model.TestCreateRequest;
//import passoff.model.TestUser;
//import passoff.server.TestServerFacade;
//import server.Server;
//
//import java.net.HttpURLConnection;
//
//public class LoginServiceTests {
//
//    private static TestUser existingUser;
//
//    private static TestUser newUser;
//
//    private static TestCreateRequest createRequest;
//
//    private static TestServerFacade serverFacade;
//    private static Server server;
//
//    private String existingAuth;
//
//    @AfterAll
//    static void stopServer() {
//        server.stop();
//    }
//
//    @BeforeAll
//    public static void init() {
//        server = new Server();
//        var port = 8080;
//        server.run(port);
//        System.out.println("Started test HTTP server on " + port);
//
//        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
//
//        existingUser = new TestUser("username", "password", "eu@mail.com");
//
//        newUser = new TestUser("NewUser", "newUserPassword", "nu@mail.com");
//
//        createRequest = new TestCreateRequest("testGame");
//    }
//
//    @BeforeEach
//    public void setup() {
//        serverFacade.clear();
//
//        //one user already logged in
//        TestAuthResult regResult = serverFacade.register(existingUser);
//        existingAuth = regResult.getAuthToken();
//    }
//
//    @Test
//    @Order(1)
//    @DisplayName("Login Service")
//    public void login() throws Exception {
//        String htmlFromServer = serverFacade.file("/").replaceAll("\r", "");
//        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
//                "Server response code was not 200 OK");
//        Assertions.assertNotNull(htmlFromServer, "Server returned an empty file");
//        Assertions.assertTrue(htmlFromServer.contains("CS 240 Chess Server Web API"));
//    }