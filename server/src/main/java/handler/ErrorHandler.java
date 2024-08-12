package handler;

import service.exceptions.ExistingUserException;
import service.exceptions.ForbiddenActionException;
import service.exceptions.InvalidCredentialsException;
import service.exceptions.MalformedRequestException;
import spark.Response;

public class ErrorHandler {
    public static String handleException(Exception e, Response response) {
        server.response.Response errorResponse = new server.response.Response();

        switch (e) {
            case MalformedRequestException malformedRequestException -> response.status(400);
            case InvalidCredentialsException invalidCredentialsException -> response.status(401);
            case ForbiddenActionException forbiddenActionException -> response.status(403);
            case ExistingUserException existingUserException -> response.status(403);
            case null, default -> response.status(500);
        }

        assert e != null;
        errorResponse.setMessage(e.getMessage());
        return Serializer.serialize(errorResponse);
    }
}
