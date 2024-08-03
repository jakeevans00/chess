package ui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerFacade {
    private static final String SERVER_URL = "localhost:8080";

    public boolean login(String[] args) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);


            String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean register(String[] args) {
        return false;
    }

    public boolean logout(String[] args) {
        return false;
    }
}
