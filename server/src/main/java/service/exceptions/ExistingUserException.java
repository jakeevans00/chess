package service.exceptions;

/*
    Thrown if a user attempts to register with an existing username
 */
public class ExistingUserException extends Exception {
    public ExistingUserException(String message) { super(message); }
}

