package server.response;

public class AuthResponse extends LoginResponse {
    public AuthResponse(String username, String authToken) {
        super(username, authToken);
    }

    public AuthResponse() {
    }
}
