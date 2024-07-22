package response;

public class CreateGameResponse extends Response {
    private int gameId;

    public CreateGameResponse(int gameId) {
        this.gameId = gameId;
    }

    public CreateGameResponse(String message) {
        super(message);
    }

    public int getGameId() {
        return gameId;
    }
}
