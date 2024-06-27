package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface ChessRuleBook {
    static Collection<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition){
        return ChessMoveRules.validMoves(board, myPosition);
    }
}
