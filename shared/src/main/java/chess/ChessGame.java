package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public ChessGameState state = new ChessGameState();

    public ChessGame() {
        getBoard().loadBoard(ChessBoard.STANDARD_BOARD);
    }

    @Override
    public String toString() {
        return state.toString();
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public static TeamColor fromString(String colorString) {
            if (colorString == null) {
                throw new IllegalArgumentException("Color string cannot be null");
            }
            return switch (colorString.toUpperCase()) {
                case "BLACK" -> BLACK;
                case "WHITE" -> WHITE;
                default -> throw new IllegalArgumentException("Invalid color: " + colorString);
            };
        }
        }

    public static TeamColor getOppositeColor (TeamColor color) {
        return TeamColor.BLACK == color ? TeamColor.WHITE : TeamColor.BLACK;
    }


    public TeamColor getTeamTurn() {
        return state.getTurn();
    }

    public void setTeamTurn(TeamColor team) {
        state.setTurn(team);
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null) {
            return null;
        }
        HashSet<ChessMove> validMoves = new HashSet<>(ChessMoveRules.pieceMoves(state.getBoard(), startPosition));
        ChessRuleBook.validateMoves(getBoard(), validMoves);
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!isLegalMove(move)) {
            throw new InvalidMoveException("Error: Illegal move: " + move);
        }

        ChessBoard board = getBoard();
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());

        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Error: Can't move opponent's piece");
        }

        board.movePiece(move);

        if (move.isCastle(piece.getPieceType())) {
           board.castle(move);
        }

        TeamColor next = getOppositeColor(piece.getTeamColor());
        if (isInCheckmate(next)) {
            state.setStatus(ChessGameState.Status.CHECKMATE);
        } else if (isInCheck(next)) {
            state.setStatus(ChessGameState.Status.CHECK);
        } else if (isInStalemate(next)) {
            state.setStatus(ChessGameState.Status.STALEMATE);
        }

        setTeamTurn(next);
    }

    public boolean isLegalMove(ChessMove move) {
        ChessBoard board = getBoard();
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());

        if (piece == null) { return false; }

        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) piece.pieceMoves(board, move.getStartPosition());
        ChessRuleBook.validateMoves(getBoard(), possibleMoves);
        return possibleMoves.contains(move) && getTeamTurn() == piece.getTeamColor();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return ChessRuleBook.isInCheck(getBoard(), teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return ChessRuleBook.isInCheckmate(getBoard(), teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return ChessRuleBook.isInStalemate(getBoard(), teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        state.setBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return state.getBoard();
    }
}
