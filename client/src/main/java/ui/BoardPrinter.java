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
                setBlack(out); // Reset to black background
            }
            out.println();
        }
    }

    private String getPiece(int row, int col) {
        // Example logic to place pieces on the board
        if (row == 1) return BLACK_PIECE; // Black pawns
        if (row == 6) return WHITE_PIECE; // White pawns
        return null; // Empty square
    }

    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
}
