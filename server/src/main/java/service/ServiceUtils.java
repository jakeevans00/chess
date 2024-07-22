package service;

import dataaccess.DataAccessException;
import handler.ErrorHandler;

import java.util.concurrent.Callable;

public class ServiceUtils {
    public static boolean isInvalidString(String str) {
        return str == null || str.length() > 20 || str.isEmpty();
    }

    public static <T> T execute(Callable<T> callable) throws DataAccessException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new DataAccessException("Error: Database can't be reached");
        }
    }
}
