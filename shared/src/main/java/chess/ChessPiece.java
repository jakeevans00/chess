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
    boolean hasMoved = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
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
        int[][] directions = {{-1, 0}, {-1, 1},{0,1},{1,1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        return getSingleMoves(board, myPosition, directions);
    }

    public HashSet<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>(getDiagonalMoves(board, myPosition));
        moves.addAll(getOrthogonalMoves(board, myPosition));
        return moves;

    }

    public HashSet<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        return getOrthogonalMoves(board, myPosition);
    }

    public HashSet<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        return getDiagonalMoves(board, myPosition);
    }

    public HashSet<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{-2, 1}, {-2, -1},{-1,2},{-1,-2}, {1, 2}, {1, -2}, {2, 1}, {2, -1}};
        return getSingleMoves(board, myPosition, directions);
    }

    public HashSet<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        int mod = (this.getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;
        List<int[]> directions = initializePawnDirections(mod, board, myPosition);

        for (int[] direction : directions) {
            ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + direction[0], myPosition.getColumn() + direction[1]);
            ChessPiece nextPiece = board.getPiece(nextPosition);

            if (isValidPawnMove(nextPiece, nextPosition, myPosition)) {
                if (nextPosition.getRow() == 1 || nextPosition.getRow() == 8) {
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, nextPosition));
                }
            }
        }
        return moves;
    }

    public HashSet<ChessMove> getDiagonalMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1,1},{-1,1},{1, -1},{-1,-1}};
        return new HashSet<>(getExtendedMoves(board, myPosition, directions));
    }

    public HashSet<ChessMove> getOrthogonalMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1,0},{0,1},{-1,0},{0,-1}};
        return new HashSet<>(getExtendedMoves(board, myPosition, directions));
    }

    public HashSet<ChessMove> getSingleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        HashSet<ChessMove> moves = new HashSet<>();

        for (int[] direction : directions) {
            ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + direction[0], myPosition.getColumn() + direction[1]);
            ChessPiece nextPiece = board.getPiece(nextPosition);

            if ((nextPiece != null && nextPiece.isAlly(this)) || nextPosition.isInvalid()) {
                continue;
            }
            moves.add(new ChessMove(myPosition, nextPosition));
        }
        return moves;
    }

    public HashSet<ChessMove> getExtendedMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        HashSet<ChessMove> moves = new HashSet<>();

        for (int[] direction : directions) {
            int nextRow = myPosition.getRow();
            int nextColumn = myPosition.getColumn();

            while (true) {
                nextRow += direction[0];
                nextColumn += direction[1];
                ChessPosition nextPosition  = new ChessPosition(nextRow, nextColumn);
                ChessPiece nextPiece = board.getPiece(nextPosition);
                ChessMove possibleMove = new ChessMove(myPosition, nextPosition);

                if ((nextPiece != null && nextPiece.isAlly(this)) || nextPosition.isInvalid()) {
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

    public List<int[]> initializePawnDirections(int mod, ChessBoard board, ChessPosition myPosition) {
        List<int[]> directions = new ArrayList<>(List.of(
                new int[] {mod, 0},   // Move one square forward
                new int[] {mod, -1},   // Capture diagonally right
                new int[] {mod, 1}   // Capture diagonally left
        ));

        ChessPiece blockPiece = board.getPiece(new ChessPosition(myPosition.getRow()+mod, myPosition.getColumn()));
        if (!hasMoved && ChessBoard.atStartingPosition(this, myPosition) && blockPiece == null) {
            directions.add(new int[] {mod * 2, 0});
        }

        return directions;
    }

    public boolean isValidPawnMove(ChessPiece nextPiece, ChessPosition nextPosition, ChessPosition myPosition) {
        return (nextPiece != null && !isAlly(nextPiece) && nextPosition.getColumn() != myPosition.getColumn()) ||
                (nextPiece == null && nextPosition.getColumn() == myPosition.getColumn());
    }

}
