package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final int BOARD_SIZE = 8;
    private static final String EMPTY_SQUARE = "   ";
    private final ChessBoard board;
    private Set<ChessPosition> highlightPositions = new HashSet<>();

    public BoardPrinter(ChessBoard board) {
        this.board = board;
    }

    public BoardPrinter(ChessBoard board, Set<ChessPosition> highlightPositions) {
        this.board = board;
        this.highlightPositions = highlightPositions;
    }

    public void drawBoard(ChessGame.TeamColor perspective) {
        try {
            out.print(ERASE_SCREEN);
            drawChessBoard(out, perspective, highlightPositions);
            out.flush();
        } catch (Exception e) {
            System.err.println("Error while drawing the board: " + e.getMessage());
        }
    }

    private void drawChessBoard(PrintStream out, ChessGame.TeamColor perspective, Set<ChessPosition> highlight) {
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
                ChessPosition position = new ChessPosition(row, col);
                if (!highlight.isEmpty() && highlight.contains(position)) {
                    setHighlight(out, row, col);
                } else {
                    if ((row + col + 1) % 2 == 0) {
                        setWhite(out);
                    } else {
                        setBlack(out);
                    }
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

    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_YELLOW);
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_YELLOW);
    }

    private void setHighlight(PrintStream out, int row, int col) {
        if ((row + col + 1) % 2 == 0) {
            out.print(SET_BG_COLOR_GREEN);
        } else {
            out.print(SET_BG_COLOR_DARK_GREEN);
        }
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void resetColors(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }
}
