package chess;

import java.util.*;

public interface ChessRuleBook {
    static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        return new HashSet<>(ChessMoveRules.pieceMoves(board, myPosition));
    }

    static Collection<ChessMove> teamMoves(ChessBoard board, ChessGame.TeamColor teamColor){
        Map<ChessPosition, ChessPiece> chessPieces = board.getChessPieces();
        Collection<ChessMove> moves = new HashSet<>();

        for (Map.Entry<ChessPosition, ChessPiece> entry : chessPieces.entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();

            if (piece.getTeamColor() == teamColor) {
                moves.addAll(piece.pieceMoves(board, position));
            }
        }
        return moves;
    }

    static void validateMoves(ChessBoard board, Collection<ChessMove> moves){
        ChessBoard copy = new ChessBoard(board);

        Iterator<ChessMove> iterator = moves.iterator();
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            ChessPiece piece = copy.getPiece(move.getStartPosition());

            copy.movePiece(move, piece.getTeamColor(), piece.getPieceType(), true);

            if (isInCheck(copy, piece.getTeamColor())) {
                iterator.remove();
            }

            copy.undoMove();
        }
    }

    static boolean isInCheck(ChessBoard board, ChessGame.TeamColor teamColor){
        ChessGame.TeamColor opponentColor = ChessGame.getOppositeColor(teamColor);
        Collection<ChessMove> opponentMoves = ChessRuleBook.teamMoves(board, opponentColor);

        for (ChessMove move : opponentMoves) {
            if (move.getEndPosition().equals(board.getPosition(new ChessPiece(teamColor, ChessPiece.PieceType.KING)))) {
                return true;
            }
        }

        return false;
    }

    static boolean isInCheckmate(ChessBoard board, ChessGame.TeamColor teamColor){
        Collection<ChessMove> moves = ChessRuleBook.teamMoves(board, teamColor);
        validateMoves(board, moves);
        return isInCheck(board, teamColor) && moves.isEmpty();
    }

    static boolean isInStalemate(ChessBoard board, ChessGame.TeamColor teamColor){
        Collection<ChessMove> moves = ChessRuleBook.teamMoves(board, teamColor);
        validateMoves(board, moves);
        return !isInCheck(board, teamColor) && moves.isEmpty();
    }
}
