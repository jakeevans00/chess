package ui;

import java.io.PrintStream;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    private static final int BOARD_SIZE = 8;
    private static final String EMPTY_SQUARE = "   ";
    private static final String BLACK_PIECE = " ♟ "; // Black pawn
    private static final String WHITE_PIECE = " ♙ "; // White pawn

    private PrintStream out;

    public BoardPrinter(PrintStream out) {
        this.out = out;
    }

    public void drawBoard() {
        out.print(ERASE_SCREEN);
        drawChessBoard(out);
    }

    private void drawChessBoard(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    // Light square
                    setWhite(out);
                } else {
                    // Dark square
                    setBlack(out);
                }

                // Print the pieces
                String piece = getPiece(row, col);
                out.print(piece != null ? piece : EMPTY_SQUARE);

                // Reset to default after printing each square
                resetColors(out);
            }
            out.println();
        }
    }

    private String getPiece(int row, int col) {
        // Place black pieces
        if (row == 0) {
            return getColumnPiece(col);
        } else if (row == 1) {
            return BLACK_PAWN; // Black pawns
        }

        // Place white pieces
        if (row == 6) {
            return WHITE_PAWN; // White pawns
        } else if (row == 7) {
            return getColumnPiece(col);
        }

        return null; // Empty square
    }

    public String getColumnPiece(int col) {
        return switch (col) {
            case 0, 7 -> WHITE_ROOK;
            case 1, 6 -> WHITE_KNIGHT;
            case 2, 5 -> WHITE_BISHOP;
            case 3 -> WHITE_QUEEN;
            case 4 -> WHITE_KING;
            default -> EMPTY_SQUARE;
        };
    }


    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void resetColors(PrintStream out) {
        // Reset the background and text colors to default
        out.print(RESET_BG_COLOR); // Replace with the default background color
        out.print(RESET_TEXT_COLOR); // Replace with the default text color
    }
}
