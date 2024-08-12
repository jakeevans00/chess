package handler;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import datastore.DataStore;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;

public class ClearHandler implements Route {
    private static ClearHandler instance;

    public ClearHandler() {}

    public static ClearHandler getInstance() {
        if (instance == null) {
            instance = new ClearHandler();
        }
        return instance;
    }

    @Override
    public String handle(Request request, Response response) throws SQLException, DataAccessException {
        DataStore dataStore = DataStore.getInstance();
        dataStore.clearAll();

        DatabaseManager.deleteAllData();

        return Serializer.serialize(null);
    }
}