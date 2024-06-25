package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType pieceType;
    ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.teamColor = pieceColor;
    }

    public String toString() {
        return pieceType.toString() + " " + teamColor.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, teamColor);
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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();

        if (board.getPiece(myPosition) == null) {
            return moves;
        }

        PieceType pieceType = board.getPiece(myPosition).getPieceType();

        moves = switch (pieceType) {
            case KING -> getKingMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
            case BISHOP -> getBishopMoves(board, myPosition);
            case KNIGHT -> getKnightMoves(board, myPosition);
            default -> getPawnMoves(board, myPosition);
        };

        return moves;
    }

    public HashSet<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public HashSet<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public HashSet<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public HashSet<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        return getDiagonalMoves(board, myPosition);
    }

    public HashSet<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public HashSet<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public HashSet<ChessMove> getDiagonalMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] directions = {{1,1},{-1,1},{1, -1},{-1,-1}};

        for (int[] direction : directions) {
            int nextRow = myPosition.getRow();
            int nextColumn = myPosition.getColumn();

            while (true) {
                nextRow += direction[0];
                nextColumn += direction[1];
                ChessPosition nextPosition  = new ChessPosition(nextRow, nextColumn);

                ChessPiece nextPiece = board.getPiece(nextPosition);
                ChessMove possibleMove = new ChessMove(myPosition, nextPosition);

                if ((nextPiece != null && nextPiece.getTeamColor() == this.teamColor) || !ChessBoard.inBounds(nextPosition)) {
                    break;
                }

                moves.add(possibleMove);

                if (nextPiece != null) {
                    break;
                }
            }
        }

        return moves;
    }
}
