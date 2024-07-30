package handler;

import com.google.gson.Gson;
import spark.Request;

public class Serializer {
    private static final Gson GSON = new Gson();

    public static <T> T deserialize(Request request, Class<T> clazz) {
        return GSON.fromJson(request.body(), clazz);
    }

    public static <T> T deserialize(Object object, Class<T> clazz) {
        return GSON.fromJson(GSON.toJson(object), clazz);
    }

    public static String serialize(Object res) {
        return GSON.toJson(res);
    }
}
