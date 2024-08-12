package server.utilities;

import chess.ChessMove;
import chess.ChessPiece;
import chess.Tuple;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


import java.io.IOException;

public class TupleAdapter extends TypeAdapter<Tuple<ChessMove, ChessPiece>> {

    @Override
    public void write(JsonWriter jsonWriter, Tuple<ChessMove, ChessPiece> tuple) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("move").value(new Gson().toJson(tuple.getFirst()));
        jsonWriter.name("piece").value(new Gson().toJson(tuple.getSecond()));
        jsonWriter.endObject();
    }

    @Override
    public Tuple<ChessMove, ChessPiece> read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        ChessMove move = null;
        ChessPiece piece = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "move":
                    move = new Gson().fromJson(jsonReader, ChessMove.class);
                    break;
                case "piece":
                    piece = new Gson().fromJson(jsonReader, ChessPiece.class);
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();

        return new Tuple<>(move, piece);
    }
}
