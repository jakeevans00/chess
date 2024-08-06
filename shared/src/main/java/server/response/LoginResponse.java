package server.response;

public class LoginResponse extends Response {
    private String username;
    private String authToken;

    public LoginResponse() {
    }

    public LoginResponse(String message) {
        super(message);
    }

    public LoginResponse(String username, String token) {
        this.username = username;
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return this.username + " " + this.authToken;
    }
}
