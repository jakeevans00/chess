package chess;

import java.util.*;

import static java.util.Map.entry;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final Map<ChessPosition, ChessPiece> chessPieces = new HashMap<>();
    private final int BOARD_SIZE = 8;
    Stack<ChessMove> history = new Stack<>();

    public ChessBoard() {
    }

    public static String STANDARD_BOARD = """
                        |r|n|b|q|k|b|n|r|
                        |p|p|p|p|p|p|p|p|
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |P|P|P|P|P|P|P|P|
                        |R|N|B|Q|K|B|N|R|
                        """;

    final static Map<Character, ChessPiece.PieceType> CHAR_TO_TYPE_MAP = Map.of(
            'p', ChessPiece.PieceType.PAWN,
            'n', ChessPiece.PieceType.KNIGHT,
            'r', ChessPiece.PieceType.ROOK,
            'q', ChessPiece.PieceType.QUEEN,
            'k', ChessPiece.PieceType.KING,
            'b', ChessPiece.PieceType.BISHOP);

    final static Map<ChessPiece, ChessPosition> STARTING_POSITIONS = Map.ofEntries(
            entry(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPosition(2,1)),
            entry(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPosition(7,1))
    );

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessPieces.put(position, piece);
    }

    public void removePiece(ChessPosition position) {
        chessPieces.remove(position);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.chessPieces.get(position);
    }

    public ChessPosition getPosition(ChessPiece piece) {
        for (Map.Entry<ChessPosition, ChessPiece> entry : chessPieces.entrySet()) {
            if (entry.getValue().equals(piece)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<ChessPosition, ChessPiece> getChessPieces() {
        return chessPieces;
    }

    public void movePiece(ChessMove move, ChessGame.TeamColor teamTurn, ChessPiece.PieceType pieceType) {
        removePiece(move.getStartPosition());
        removePiece(move.getEndPosition());
        addPiece(move.getEndPosition(), new ChessPiece(teamTurn, pieceType));
        history.push(move);
    }

    public static boolean atStartingPosition(ChessPiece piece, ChessPosition position) {
        ChessPosition expectedPosition = ChessBoard.STARTING_POSITIONS.get(piece);
        return expectedPosition.getRow() == position.getRow();
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        loadBoard(STANDARD_BOARD);
    }

    public String toString() {
        return chessPieces.toString();
    }

    public static int getBoardSize(ChessBoard board) {
        return board.BOARD_SIZE;
    }

    public void loadBoard(String boardText) {
        chessPieces.clear();

        int row = 8;
        int column = 1;
        for (var c : boardText.toCharArray()) {
            switch (c) {
                case '\n' -> {
                    column = 1;
                    row--;
                }
                case ' ' -> column++;
                case '|' -> {
                }
                default -> {
                    ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;
                    var type = CHAR_TO_TYPE_MAP.get(Character.toLowerCase(c));
                    var position = new ChessPosition(row, column);
                    var piece = new ChessPiece(color, type);
                    chessPieces.put(position, piece);
                    column++;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(chessPieces, that.chessPieces);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chessPieces);
    }


}
