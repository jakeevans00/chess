package server.utilities;

import chess.ChessPosition;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ChessPositionAdapter extends TypeAdapter<ChessPosition> {

    @Override
    public ChessPosition read(JsonReader jsonReader) throws IOException {
        String posStr = jsonReader.nextString();
        posStr = posStr.replace("(", "").replace(")", "").trim();
        String[] parts = posStr.split(",");
        int row = Integer.parseInt(parts[0].trim());
        int column = Integer.parseInt(parts[1].trim());
        return new ChessPosition(row, column);
    }

    @Override
    public void write(JsonWriter jsonWriter, ChessPosition position) throws IOException {
        if (position == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value("(" + position.getRow() + ", " + position.getColumn() + ")");
        }
    }

}
