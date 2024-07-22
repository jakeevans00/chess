package service;

public class HelperService {
    public static boolean isInvalidString(String str) {
        return str == null || str.length() > 20 || str.isEmpty();
    }
}
