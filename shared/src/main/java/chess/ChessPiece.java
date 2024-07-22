package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType pieceType;
    private final ChessGame.TeamColor teamColor;
    private boolean hasMoved = false;
    private int moveCount = 0;


    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this(pieceColor, type, false, 0);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type, boolean hasMoved, int moveCount) {
        this.pieceType = type;
        this.teamColor = pieceColor;
        this.hasMoved = hasMoved;
        this.moveCount = moveCount;
    }


    public String toString() {
        return pieceType.toString() + " " + teamColor.toString();
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Determines whether another piece is an ally or enemy
     * @param piece the piece being evaluated
     * @return true for ally, false for enemy
     */
    public boolean isAlly(ChessPiece piece) {
        return this.teamColor == piece.teamColor;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return ChessRuleBook.pieceMoves(board, myPosition);
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public void decrementMoveCount() {
        --this.moveCount;
        if (this.moveCount == 0) {
            this.hasMoved = false;
        }
    }

    public void incrementMoveCount() {
        this.moveCount++;
        this.hasMoved = true;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }


        @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, teamColor);
    }
}
