package chess;

public class ChessGameState {
    private ChessGame.TeamColor turn = ChessGame.TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    ChessGameState() {
    }

    @Override
    public String toString() {
        return "Chess Game: " + turn.toString() + "'s turn, board: " + board.toString();
    }

    public ChessBoard getBoard() { return board; }

    public void setBoard(ChessBoard board) { this.board = board; }

    public ChessGame.TeamColor getTurn() { return turn; }

    public void setTurn(ChessGame.TeamColor turn) { this.turn = turn; }

}
