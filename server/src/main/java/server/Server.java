package server;

import handler.*;
import spark.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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
