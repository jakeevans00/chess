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
        state.getBoard().loadBoard(ChessBoard.STANDARD_BOARD);
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

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessBoard board = state.getBoard();
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) { throw new InvalidMoveException("Invalid move"); }

        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) piece.pieceMoves(board, move.getStartPosition());
        if (!possibleMoves.contains(move) || getTeamTurn() != piece.getTeamColor()) { throw new InvalidMoveException(); }

        ChessPiece.PieceType pieceType = (move.promotionPiece != null ? move.getPromotionPiece() : piece.getPieceType());
        ChessGame.TeamColor teamTurn = piece.getTeamColor();
        if (isInCheck(teamTurn) && (pieceType != ChessPiece.PieceType.KING)) { throw new InvalidMoveException(); }

        board.movePiece(move, teamTurn, pieceType);
        setTeamTurn(getOpponentColor());
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard board = state.getBoard();
        Map<ChessPosition, ChessPiece> chessPieces = board.getChessPieces();
        Collection<ChessMove> opponentPositions = new HashSet<>();

        // Find all Enemy ChessPositions
        for (Map.Entry<ChessPosition, ChessPiece> entry : chessPieces.entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();

            if (piece.getTeamColor() != teamColor) {
                opponentPositions.addAll(piece.pieceMoves(board, position));
            }
        }

        // If King is in set of enemy positions, return true
        for (ChessMove move : opponentPositions) {
            if (move.getEndPosition().equals(board.getPosition(new ChessPiece(teamColor, ChessPiece.PieceType.KING)))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
