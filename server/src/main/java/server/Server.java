package server;

import dataaccess.*;
import handler.*;
import server.response.Response;
import spark.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {
    private final WebSocketHandler webSocketHandler;

    public Server() {
        this.webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        configureDatabase();
        Spark.webSocket("/ws", webSocketHandler);
        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes() {
        AuthDAO authDAO = new MySQLAuthDAO();

        Spark.before((request, response) -> {
            String path = request.pathInfo();

            if (!path.equals("/") &&
                    !path.equals("/user") &&
                    !(path.equals("/session") && "POST".equals(request.requestMethod())) &&
                    !path.equals("/db") &&
                    !path.equals("/ws")) {
                String authToken = request.headers("authorization");
                if (authToken == null || authDAO.getAuth(authToken) == null) {
                    Response authResponse = new Response("Error: Not Authenticated");
                    Spark.halt(401, Serializer.serialize(authResponse));
                }
            }
        });

        Spark.get("/", ((request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/public/index.html")));
        } ));

        Spark.post("/user", (req, res) -> RegisterHandler.getInstance().handle(req,res));
        Spark.post("/session", (req, res) -> LoginHandler.getInstance().handle(req,res));
        Spark.delete("/session", (req, res) -> LogoutHandler.getInstance().handle(req,res));

        Spark.get("/game", (req, res) -> ListGamesHandler.getInstance().handle(req,res));
        Spark.post("/game", (req, res) -> CreateGameHandler.getInstance().handle(req,res));
        Spark.put("/game", (req, res) -> JoinGameHandler.getInstance().handle(req,res));

        Spark.delete("/db", (req, res) -> ClearHandler.getInstance().handle(req, res));
    }

    private void configureDatabase() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }
}
