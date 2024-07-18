package server;

import handler.LoginHandler;
import spark.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
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

        Spark.post("/session", (req, res) -> (new LoginHandler().handle(req,res)));
    }
}
