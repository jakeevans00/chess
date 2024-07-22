package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import response.CreateGameResponse;
import service.exceptions.MalformedRegistrationException;

public class GameService {
    public CreateGameResponse createGame(GameData gameData) throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();

        if (gameData == null || HelperService.isInvalidString(gameData.gameName())) {
            throw new MalformedRegistrationException("Error: Invalid game name");
        }

        try {
            gameDAO.addGame(gameData);
            return new CreateGameResponse(gameData.gameID());
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to reach database");
        }
    }


}
