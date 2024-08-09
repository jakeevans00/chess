package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.lang.reflect.Field;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    private static final int BOARD_SIZE = 8;
    private static final String EMPTY_SQUARE = "   ";
    private ChessBoard board;

    private final PrintStream out;

    public BoardPrinter(PrintStream out, ChessBoard board) {
        this.out = out;
        this.board = board;
    }

    public void drawBoard(ChessGame.TeamColor perspective) {
        try {
            out.print(ERASE_SCREEN);
            drawChessBoard(out, perspective);
            out.flush();
        } catch (Exception e) {
            System.err.println("Error while drawing the board: " + e.getMessage());
        }
    }

    private void drawChessBoard(PrintStream out, ChessGame.TeamColor perspective) {
        boolean whitePerspective = perspective.equals(ChessGame.TeamColor.WHITE);
        char startLetter = whitePerspective ? 'a' : 'h';
        char endLetter = whitePerspective ? 'h' : 'a';

        out.print("  ");
        for (char letter = startLetter;
             (whitePerspective ? letter <= endLetter : letter >= endLetter);
             letter = (char) (letter + (whitePerspective ? 1 : -1))) {
            out.print(" " + letter + " ");
        }
        out.println();

        for (int row = BOARD_SIZE; row >= 1; row--) {

            if (whitePerspective) {
                out.print(row + " ");
            } else {
                out.print(BOARD_SIZE - row + 1 + " ");
            }

            for (int col = 1; col <= BOARD_SIZE; col++) {
                if ((row + col + 1) % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }

                String piece = getPiece(row, col, whitePerspective);
                out.print(piece != null ? piece : EMPTY_SQUARE);

                resetColors(out);
            }
            out.println();
        }
    }

    private String getPiece(int row, int col, boolean whitePerspective) {
        int adjustedRow = whitePerspective ? row : (BOARD_SIZE + 1 - row);
        int adjustedCol = whitePerspective ? col : (BOARD_SIZE + 1 - col);

        ChessPiece piece = board.getPiece(new ChessPosition(adjustedRow, adjustedCol));
        if (piece == null) {
            return EMPTY_SQUARE;
        }

        String pieceName = piece.toString();

        try {
            Field field = EscapeSequences.class.getField(pieceName);
            return (String) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return EMPTY_SQUARE;
        }
    }

    public String getColumnPiece(int col, boolean isBlack) {
        return switch (col) {
            case 1, 8 -> isBlack ? BLACK_ROOK : WHITE_ROOK;
            case 2, 7 -> isBlack ? BLACK_KNIGHT : WHITE_KNIGHT;
            case 3, 6 -> isBlack ? BLACK_BISHOP : WHITE_BISHOP;
            case 4 -> isBlack ? BLACK_QUEEN : WHITE_QUEEN;
            case 5 -> isBlack ? BLACK_KING : WHITE_KING;
            default -> EMPTY_SQUARE;
        };
    }


    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_YELLOW);
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_YELLOW);
    }

    private void resetColors(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }
}
