package server.response;

public class CreateGameResponse extends Response {
    private int gameID;

    public CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }

    public CreateGameResponse(String message) {
        super(message);
    }

    public int getGameID() {
        return gameID;
    }
}
