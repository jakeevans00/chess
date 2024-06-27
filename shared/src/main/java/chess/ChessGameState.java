package chess;

public class ChessGameState {
    private ChessGame.TeamColor turn = ChessGame.TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();
    private ChessGameState.GameStatus status = GameStatus.IN_PROGRESS;

    ChessGameState() {
    }

    public ChessBoard getBoard() { return board; }

    public void setBoard(ChessBoard board) { this.board = board; }

    public ChessGame.TeamColor getTurn() { return turn; }

    public void setTurn(ChessGame.TeamColor turn) { this.turn = turn; }

    public ChessGameState.GameStatus getStatus() { return status; }

    public void setStatus(ChessGameState.GameStatus status) { this.status = status; }

    public enum GameStatus {
        WON_BLACK,WON_WHITE,DRAW,IN_PROGRESS
    }
}
