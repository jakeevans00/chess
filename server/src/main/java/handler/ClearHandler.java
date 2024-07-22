package handler;

import datastore.DataStore;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private static ClearHandler instance;

    private ClearHandler() {}

    public static ClearHandler getInstance() {
        if (instance == null) {
            instance = new ClearHandler();
        }
        return instance;
    }

    @Override
    public String handle(Request request, Response response) {
        DataStore dataStore = DataStore.getInstance();
        dataStore.clearAll();

        return Serializer.serialize(null);
    }
}