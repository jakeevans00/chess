package handler;

import service.exceptions.ExistingUserException;
import service.exceptions.MalformedRegistrationException;
import spark.Response;

public class ErrorHandler {
    public static String handleException(Exception e, Response response) {
        if (e instanceof MalformedRegistrationException) {
            response.status(400);
        } else if (e instanceof ExistingUserException) {
            response.status(403);
        } else {
            response.status(500);
        }
        return Serializer.serialize(e.getMessage());
    }
}
