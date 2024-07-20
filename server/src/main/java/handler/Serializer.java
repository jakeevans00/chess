package handler;

import com.google.gson.Gson;
import spark.Request;

public class Serializer {
    private static final Gson gson = new Gson();

    public static <T> T deserialize(Request request, Class<T> clazz) {
        return gson.fromJson(request.body(), clazz);
    }

    public static String serialize(Object res) {
        return gson.toJson(res);
    }
}
