package chess;

public class ChessGameState {
    private ChessGame.TeamColor turn = ChessGame.TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();
    private Status status;


    ChessGameState() {
        this.status  = Status.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return "Chess Game: " + turn.toString() + "'s turn, board: " + board.toString();
    }

    public enum Status {
            IN_PROGRESS, PAUSED, CHECKMATE, STALEMATE, RESIGNED, CHECK
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public ChessBoard getBoard() { return board; }

    public void setBoard(ChessBoard board) { this.board = board; }

    public ChessGame.TeamColor getTurn() { return turn; }

    public void setTurn(ChessGame.TeamColor turn) { this.turn = turn; }

}
