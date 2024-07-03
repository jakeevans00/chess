package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessGameState state = new ChessGameState();

    public ChessGame() {
        getBoard().loadBoard(ChessBoard.STANDARD_BOARD);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return state.getTurn();
    }

    /**
     * @return Opponent's color
     */
    public TeamColor getOpponentColor() {
        return getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        state.setTurn(team);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public static Enum<TeamColor> getOppositeColor (TeamColor color) {
        return TeamColor.BLACK == color ? TeamColor.WHITE : TeamColor.BLACK;
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
        HashSet<ChessMove> validMoves = new HashSet<>(ChessRuleBook.pieceMoves(state.getBoard(), startPosition));
        ChessRuleBook.validateMoves(getBoard(), validMoves);
        return validMoves;
    }

    public Collection<ChessMove> teamValidMoves(TeamColor teamColor) {
        return new HashSet<>(ChessRuleBook.teamMoves(getBoard(), teamColor));
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessBoard board = getBoard();
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) { throw new InvalidMoveException("Invalid move"); }

        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) piece.pieceMoves(board, move.getStartPosition());
        if (!possibleMoves.contains(move) || getTeamTurn() != piece.getTeamColor()) { throw new InvalidMoveException(); }

        ChessPiece.PieceType pieceType = (move.promotionPiece != null ? move.getPromotionPiece() : piece.getPieceType());
        ChessGame.TeamColor teamTurn = piece.getTeamColor();
        if (isInCheck(teamTurn) && (pieceType != ChessPiece.PieceType.KING)) { throw new InvalidMoveException(); }

        board.movePiece(move, teamTurn, pieceType, true);
        setTeamTurn(getOpponentColor());
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
