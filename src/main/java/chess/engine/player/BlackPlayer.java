package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.pieces.Piece;

import java.util.Collection;

public class BlackPlayer extends Player {
    public BlackPlayer(Board board,
                       Collection<Move> blackStandardLegalMoves,
                       Collection<Move> whiteStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return board.whitePlayer();
    }
}
