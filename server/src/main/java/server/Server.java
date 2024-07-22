package server;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import handler.*;
import spark.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void createRoutes() {
        AuthDAO authDAO = new MemoryAuthDAO();

        Spark.before((request, response) -> {
            String path = request.pathInfo();

            if (!path.equals("/") &&
                    !path.equals("/user") &&
                    !(path.equals("/session") && "POST".equals(request.requestMethod())) &&
                    !path.equals("/db")) {
                String authToken = request.headers("authorization");
                if (authToken == null || authDAO.getAuth(authToken) == null) {
                    response.Response authResponse = new response.Response("Error: Not Authenticated");
                    Spark.halt(401, Serializer.serialize(authResponse));
                }
            }
        });

        Spark.get("/", ((request, response) -> {
            response.type("text/html");
            return new String(Files.readAllBytes(Paths.get("src/main/resources/public/index.html")));
        } ));

        Spark.post("/user", (req, res) -> (new RegisterHandler().handle(req,res)));
        Spark.post("/session", (req, res) -> (new LoginHandler().handle(req,res)));
        Spark.delete("/session", (req, res) -> (new LogoutHandler().handle(req,res)));

        Spark.get("/game", (req, res) -> (new ListGamesHandler().handle(req,res)));
        Spark.post("/game", (req, res) -> (new CreateGameHandler().handle(req,res)));
        Spark.put("/game", (req, res) -> (new JoinGameHandler().handle(req,res)));

        Spark.delete("/db", (req, res) -> (new ClearHandler().handle(req,res)));
    }
}
