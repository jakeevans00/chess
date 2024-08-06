package ui;

import java.io.PrintStream;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    private static final int BOARD_SIZE = 8;
    private static final String EMPTY_SQUARE = "   ";

    private final PrintStream out;

    public BoardPrinter(PrintStream out) {
        this.out = out;
    }

    public void drawBoard() {
        try {
            out.print(ERASE_SCREEN);
            drawChessBoard(out);
            out.flush();
        } catch (Exception e) {
            System.err.println("Error while drawing the board: " + e.getMessage());
        }
    }

    private void drawChessBoard(PrintStream out) {
        out.print("  ");
        for (char letter = 'a'; letter <= 'h'; letter++) {
            out.print(" " + letter + " ");
        }
        out.println();

        for (int row = 0; row < BOARD_SIZE; row++) {
            out.print((BOARD_SIZE - row) + " ");

            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }

                String piece = getPiece(row, col);
                out.print(piece != null ? piece : EMPTY_SQUARE);

                resetColors(out);
            }
            out.println();
        }
    }

    private String getPiece(int row, int col) {
        if (row == 0) {
            return getColumnPiece(col, true);
        } else if (row == 1) {
            return BLACK_PAWN;
        } else if (row == 6) {
            return WHITE_PAWN;
        } else if (row == 7) {
            return getColumnPiece(col, false);
        }

        return null;
    }

    public String getColumnPiece(int col, boolean isBlack) {
        return switch (col) {
            case 0, 7 -> isBlack ? BLACK_ROOK : WHITE_ROOK;
            case 1, 6 -> isBlack ? BLACK_KNIGHT : WHITE_KNIGHT;
            case 2, 5 -> isBlack ? BLACK_BISHOP : WHITE_BISHOP;
            case 3 -> isBlack ? BLACK_QUEEN : WHITE_QUEEN;
            case 4 -> isBlack ? BLACK_KING : WHITE_KING;
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
