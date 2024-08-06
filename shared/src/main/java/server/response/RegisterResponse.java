package server.response;

public class RegisterResponse extends LoginResponse {
    public RegisterResponse(String username, String authToken) {
        super(username, authToken);
    }

    public RegisterResponse() {
    }
}
