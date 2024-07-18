package handler;

import datastore.DataStore;
import model.UserData;
import response.LoginResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    public String handle(Request request, Response response) {
        DataStore dataStore = DataStore.getInstance();
        dataStore.clearAll();

        return Serializer.deserialize(null);
    }
}