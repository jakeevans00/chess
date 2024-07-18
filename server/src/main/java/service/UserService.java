package service;

import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import response.LoginResponse;

import java.util.UUID;

public class UserService {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();

    public AuthData register(UserData user) {throw new RuntimeException(); }


    public LoginResponse login(UserData user) {
        LoginResponse response = new LoginResponse();

        if (userDAO.getUser(user.username()) == null) {
           response.setMessage("User not found");
           return response;
        }

        if (!userDAO.getUser(user.username()).password().equals(user.password())) {
            response.setMessage("Wrong password, try again");
            return response;
        }


        return new LoginResponse(UUID.randomUUID().toString(), user.username());
    }

    public void logout(AuthData authData) {}
}
