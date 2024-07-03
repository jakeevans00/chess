package chess;

public class ChessGameState {
    private ChessGame.TeamColor turn = ChessGame.TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    ChessGameState() {
    }

    public ChessBoard getBoard() { return board; }

    public void setBoard(ChessBoard board) { this.board = board; }

    public ChessGame.TeamColor getTurn() { return turn; }

    public void setTurn(ChessGame.TeamColor turn) { this.turn = turn; }

}
