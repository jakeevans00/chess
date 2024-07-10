package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ChessMoveRules implements ChessRuleBook {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();

        if (board.getPiece(myPosition) == null) {
            return moves;
        }

        ChessPiece.PieceType pieceType = board.getPiece(myPosition).getPieceType();

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

    public static HashSet<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{-1, 0}, {-1, 1},{0,1},{1,1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        HashSet<ChessMove> moves = getSingleMoves(board, myPosition, directions);
        moves.addAll(getCastling(board, myPosition));
        return moves;
    }

    public static HashSet<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>(getDiagonalMoves(board, myPosition));
        moves.addAll(getOrthogonalMoves(board, myPosition));
        return moves;

    }

    public static HashSet<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        return getOrthogonalMoves(board, myPosition);
    }

    public static HashSet<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        return getDiagonalMoves(board, myPosition);
    }

    public static HashSet<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{-2, 1}, {-2, -1},{-1,2},{-1,-2}, {1, 2}, {1, -2}, {2, 1}, {2, -1}};
        return getSingleMoves(board, myPosition, directions);
    }

    public static HashSet<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece piece = board.getPiece(myPosition);
        int mod = (piece.getTeamColor()== ChessGame.TeamColor.WHITE ? 1 : -1);

        List<int[]> directions = initializePawnDirections(mod, board, myPosition);

        for (int[] direction : directions) {
            ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + direction[0], myPosition.getColumn() + direction[1]);
            ChessPiece nextPiece = board.getPiece(nextPosition);

            if (isValidPawnMove(board, nextPiece, nextPosition, myPosition)) {
                if (nextPosition.getRow() == 1 || nextPosition.getRow() == 8) {
                    moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, nextPosition));
                }
            }

            moves.addAll(getEnPassant(mod, board, myPosition));
        }
        return moves;
    }

    public static HashSet<ChessMove> getDiagonalMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1,1},{-1,1},{1, -1},{-1,-1}};
        return new HashSet<>(getExtendedMoves(board, myPosition, directions));
    }

    public static HashSet<ChessMove> getOrthogonalMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1,0},{0,1},{-1,0},{0,-1}};
        return new HashSet<>(getExtendedMoves(board, myPosition, directions));
    }

    public static HashSet<ChessMove> getSingleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece thisPiece = board.getPiece(myPosition);

        for (int[] direction : directions) {
            ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + direction[0], myPosition.getColumn() + direction[1]);
            ChessPiece nextPiece = board.getPiece(nextPosition);

            if ((nextPiece != null && nextPiece.isAlly(thisPiece)) || nextPosition.isInvalid()) {
                continue;
            }
            moves.add(new ChessMove(myPosition, nextPosition));
        }
        return moves;
    }

    public static HashSet<ChessMove> getExtendedMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece thisPiece = board.getPiece(myPosition);


        for (int[] direction : directions) {
            int nextRow = myPosition.getRow();
            int nextColumn = myPosition.getColumn();

            while (true) {
                nextRow += direction[0];
                nextColumn += direction[1];
                ChessPosition nextPosition  = new ChessPosition(nextRow, nextColumn);
                ChessPiece nextPiece = board.getPiece(nextPosition);
                ChessMove possibleMove = new ChessMove(myPosition, nextPosition);

                if ((nextPiece != null && nextPiece.isAlly(thisPiece)) || nextPosition.isInvalid()) {
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

    public static List<int[]> initializePawnDirections(int mod, ChessBoard board, ChessPosition myPosition) {
        boolean hasNotMoved = (mod == 1 && myPosition.getRow() == 2) || (mod == -1 && myPosition.getRow() == 7);
        List<int[]> directions = new ArrayList<>(List.of(
                new int[] {mod, 0},   // Move one square forward
                new int[] {mod, -1},   // Capture diagonally right
                new int[] {mod, 1}   // Capture diagonally left
        ));

        ChessPiece blockPiece = board.getPiece(new ChessPosition(myPosition.getRow()+mod, myPosition.getColumn()));
        if (hasNotMoved && blockPiece == null) {
            directions.add(new int[] {mod * 2, 0});
        }

        return directions;
    }

    public static boolean isValidPawnMove(ChessBoard board, ChessPiece nextPiece, ChessPosition nextPosition, ChessPosition myPosition) {
        ChessPiece thisPiece = board.getPiece(myPosition);

        return (nextPiece != null && !nextPiece.isAlly(thisPiece) && nextPosition.getColumn() != myPosition.getColumn()) ||
                (nextPiece == null && nextPosition.getColumn() == myPosition.getColumn());
    }

    public static HashSet<ChessMove> getCastling(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        int row = myPosition.getRow();

        ChessPiece kingPiece = board.getPiece(new ChessPosition(row, myPosition.getColumn()));
        ChessPiece leftRookPiece = board.getPiece(new ChessPosition(row, 1));
        ChessPiece rightRookPiece = board.getPiece(new ChessPosition(row, 8));

        if (kingPiece != null && !kingPiece.hasMoved()) {
            if (leftRookPiece != null && !leftRookPiece.hasMoved() && getExtendedMoves(board, myPosition, new int[][]{{0,-1}}).size() == 3) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, 3)));
            }
            if (rightRookPiece != null && !rightRookPiece.hasMoved() && getExtendedMoves(board, myPosition, new int[][]{{0,1}}).size() == 2) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, 7)));
            }
        }
        // check to see if results in piece being inCheck (inDanger)

        return moves;
    }

    public static HashSet<ChessMove> getEnPassant(int mod, ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        if (board.history.isEmpty()) {
            return moves;
        }

        ChessMove prevMove = board.history.peek().first();
        if (prevMove == null) {
            return moves;
        }

        ChessPiece prevMovePiece = board.getPiece(prevMove.getEndPosition());
        int prevMoveEndRow = prevMove.getEndPosition().getRow();
        int distance = Math.abs(prevMove.getStartPosition().getRow() - prevMove.getEndPosition().getRow());
        if (prevMovePiece != null && prevMovePiece.getPieceType() == ChessPiece.PieceType.PAWN && distance == 2 && myPosition.getRow() == prevMoveEndRow) {
            moves.add(new ChessMove(myPosition, new ChessPosition(prevMove.getEndPosition().getRow() + mod, prevMove.getEndPosition().getColumn())));
        }

        return moves;
    }
}
