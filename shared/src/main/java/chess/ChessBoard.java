package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final Map<ChessPosition, ChessPiece> chessPieces = new HashMap<>();
    Stack<Tuple<ChessMove, ChessPiece>> history = new Stack<>();

    public ChessBoard() {
    }

    public ChessBoard(ChessBoard copy) {
        this.chessPieces.putAll(copy.chessPieces);
        this.history = copy.history;
    }

    public static final String STANDARD_BOARD = """
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

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessPieces.put(position, piece);
    }

    public ChessPiece removePiece(ChessPosition position) {
        return chessPieces.remove(position);
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

    public void movePiece(ChessMove move) {
        ChessPiece movedPiece = removePiece(move.getStartPosition());
        ChessGame.TeamColor teamColor = movedPiece.getTeamColor();
        ChessPiece.PieceType pieceType = (move.promotionPiece != null ? move.getPromotionPiece() : movedPiece.getPieceType());

        movedPiece.incrementMoveCount();

        // Pawn Promotion
        if (move.getPromotionPiece() != null) {
            movedPiece = new ChessPiece(teamColor, pieceType, true, movedPiece.getMoveCount());
         }

        ChessPiece capturedPiece = chessPieces.put(move.getEndPosition(), movedPiece);

        // En Passant
        if (movedPiece.getPieceType() == ChessPiece.PieceType.PAWN &&
            capturedPiece == null && move.getStartPosition().getColumn() != move.getEndPosition().getColumn()) {
            int mod = movedPiece.getTeamColor() == ChessGame.TeamColor.BLACK ? -1 : 1;
            ChessPosition position = new ChessPosition(move.getEndPosition().getRow()-mod, move.getEndPosition().getColumn());
            capturedPiece = removePiece(position);
        }

        history.push(new Tuple<>(move, capturedPiece));
    }

    public void castle(ChessMove move) {
        int row = move.getStartPosition().getRow();
        ChessMove castle = ChessRuleBook.isCastleLeft(move) ?
                  new ChessMove(new ChessPosition(row, 1), new ChessPosition(row, 4)) :
                  new ChessMove(new ChessPosition(row, 8), new ChessPosition(row, 6));
        movePiece(castle);
    }

    public void undoMove() {
        if (history.isEmpty()) {
            throw new IllegalStateException("No moves to undo");
        }

        Tuple<ChessMove, ChessPiece> change = history.pop();
        ChessMove move = change.first();
        ChessPiece removedPiece = change.second();

        ChessPiece movedPiece = removePiece(move.getEndPosition());
        movedPiece.decrementMoveCount();

        if (change.first().getPromotionPiece() != null) {
            movedPiece = new ChessPiece(movedPiece.getTeamColor(), ChessPiece.PieceType.PAWN);
            movedPiece.setMoveCount(5);
        }

        addPiece(move.getStartPosition(), movedPiece);

        if (removedPiece != null) {
            addPiece(move.getEndPosition(), removedPiece);
        }

        if (move.isCastle(movedPiece.getPieceType())) {
            undoMove();
        }
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
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(chessPieces, that.chessPieces);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chessPieces);
    }
}
