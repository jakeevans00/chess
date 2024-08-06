package server;

import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import request.JoinGameRequest;
import response.*;
import utilities.ChessPositionAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public RegisterResponse register(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, null, user, RegisterResponse.class);
    }

    public LoginResponse login(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, null, user, LoginResponse.class);
    }

    public void logout(String token) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, token, null, LogoutResponse.class);
    }

    public void createGame(String token, GameData game) throws ResponseException {
        var path = "/game";
        this.makeRequest("POST", path, token, game, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(String token) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, token, null, ListGamesResponse.class);
    }

    public void joinGame(String token, JoinGameRequest request) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, token, request, JoinGameResponse.class);
    }

    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeHeader(authToken, http);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeHeader(String authToken, HttpURLConnection http) throws IOException {
        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            String errorMessage = readErrorResponse(http);
            throw new ResponseException(status, errorMessage);
        }
    }

    private String readErrorResponse(HttpURLConnection http) throws IOException {
        InputStream errorStream = http.getErrorStream();
        Gson gson = new Gson();

        if (errorStream != null) {
            try (InputStreamReader reader = new InputStreamReader(errorStream);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                StringBuilder errorBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    errorBuilder.append(line);
                }
                Response response = gson.fromJson(errorBuilder.toString(), Response.class);
                return response.getMessage();
            }
        }
        return "Unknown error";
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter())
                .create();
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = gson.fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
