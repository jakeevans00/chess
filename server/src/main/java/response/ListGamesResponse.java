package response;

import model.GameData;

import java.util.List;

public class ListGamesResponse {
    private final List<GameData> games;

    public ListGamesResponse(List<GameData> games) {
        this.games = games;
    }

    public List<GameData> getGames() {
        return games;
    }
}
