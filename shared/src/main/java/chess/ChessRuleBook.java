package chess;

import java.util.*;

public class ChessRuleBook {
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

    static void validateMoves(ChessBoard board, Collection<ChessMove> moves) {
        Iterator<ChessMove> iterator = moves.iterator();
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            ChessPiece piece = board.getPiece(move.getStartPosition());

            if (piece == null) {
                continue;
            }

            if (move.isCastle(piece.getPieceType())) {
                handleCastleMove(board, iterator, move, piece);
            } else {
                handleRegularMove(board, iterator, move, piece);
            }
        }
    }

    private static void handleCastleMove(ChessBoard board, Iterator<ChessMove> iterator, ChessMove move, ChessPiece piece) {
        int row = move.getStartPosition().getRow();
        ChessMove castleMove = isCastleLeft(move)
                ? new ChessMove(new ChessPosition(row, 1), new ChessPosition(row, 4))
                : new ChessMove(new ChessPosition(row, 8), new ChessPosition(row, 6));

        board.movePiece(castleMove);
        board.movePiece(move);

        if (isInCheck(board, piece.getTeamColor()) || isInDanger(board, castleMove.getEndPosition())) {
            iterator.remove();
        }
        board.undoMove();
    }

    private static void handleRegularMove(ChessBoard board, Iterator<ChessMove> iterator, ChessMove move, ChessPiece piece) {
        board.movePiece(move);

        if (isInCheck(board, piece.getTeamColor())) {
            iterator.remove();
        }
        board.undoMove();
    }


    static boolean isCastleLeft(ChessMove move) {
        return move.getEndPosition().getColumn() < move.getStartPosition().getColumn();
    }

    static boolean isCastle(ChessPiece piece, ChessMove move) {
        return piece.getPieceType() == ChessPiece.PieceType.KING &&
               Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 2;
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

    static boolean isInDanger(ChessBoard board, ChessPosition position){
        ChessGame.TeamColor opponentColor = ChessGame.getOppositeColor(board.getPiece(position).getTeamColor());
        Collection<ChessMove> opponentMoves = ChessRuleBook.teamMoves(board, opponentColor);

        for (ChessMove move : opponentMoves) {
            if (move.getEndPosition().equals(position)) {
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
