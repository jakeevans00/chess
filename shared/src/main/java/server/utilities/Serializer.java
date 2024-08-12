package server.utilities;

import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;

public class Serializer {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter())
            .create();

    public static <T> T deserialize(Request request, Class<T> clazz) {
        return GSON.fromJson(request.body(), clazz);
    }

    public static <T> T fromJson(String message, Class<T> clazz) {
        return GSON.fromJson(message, clazz);
    }

    public static <T> T deserialize(Object object, Class<T> clazz) {
        return GSON.fromJson(GSON.toJson(object), clazz);
    }

    public static String serialize(Object res) {
        return GSON.toJson(res);
    }
}
