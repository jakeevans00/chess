package handler;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import spark.Request;
import spark.Response;

import java.io.File;

public class Serializer {
    private static final Gson gson = new Gson();

    public static <T> T serialize(Request request, Class<T> clazz) {
        return gson.fromJson(request.body(), clazz);
    }

    public static String deserialize(Object res) {
        return gson.toJson(res);
    }
}
