package handler;

import service.exceptions.ExistingUserException;
import service.exceptions.ForbiddenActionException;
import service.exceptions.InvalidCredentialsException;
import service.exceptions.MalformedRegistrationException;
import spark.Response;

public class ErrorHandler {
    public static String handleException(Exception e, Response response) {
        response.Response errorResponse = new response.Response();

        switch (e) {
            case MalformedRegistrationException _ -> response.status(400);
            case InvalidCredentialsException _ -> response.status(401);
            case ExistingUserException _, ForbiddenActionException _ -> response.status(403);
            case null, default -> response.status(500);
        }

        assert e != null;
        errorResponse.setMessage(e.getMessage());
        return Serializer.serialize(errorResponse);
    }
}
