package response;

public class CreateGameResponse extends Response {
    private int gameID;

    public CreateGameResponse(int gameId) {
        this.gameID = gameId;
    }

    public CreateGameResponse(String message) {
        super(message);
    }

    public int getGameId() {
        return gameID;
    }
}
