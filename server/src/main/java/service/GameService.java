package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import datastore.DataStore;
import model.GameData;
import response.CreateGameResponse;
import service.exceptions.MalformedRegistrationException;

public class GameService {
    public CreateGameResponse createGame(GameData gameDataRequest) throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();

        if (gameDataRequest == null || HelperService.isInvalidString(gameDataRequest.gameName())) {
            throw new MalformedRegistrationException("Error: Invalid game name");
        }

        try {
            ChessGame newGame = new ChessGame();
            GameData game = new GameData(DataStore.getInstance().getNextCount(), null, null, gameDataRequest.gameName(),newGame);
            gameDAO.addGame(game);
            return new CreateGameResponse(game.gameID());
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to reach database");
        }
    }


}
