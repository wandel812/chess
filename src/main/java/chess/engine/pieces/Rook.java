package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class Rook extends Piece {
    private final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-8, -1, 8, 1};

    public Rook(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.ROOK, piecePosition, pieceAlliance, true);
    }

    public Rook(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove) {
        super(PieceType.ROOK ,piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition + candidateCoordinateOffset;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(this.piecePosition, candidateCoordinateOffset) ||
                        isEighthColumnExclusion(this.piecePosition, candidateCoordinateOffset)) {
                    break;
                }
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board,
                                this,
                                candidateDestinationCoordinate,
                                pieceAtDestination));
                    }
                    break;
                }
                candidateDestinationCoordinate += candidateCoordinateOffset;
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Rook movePiece(final Move move) {
        return new Rook(move.getDestinationCoordinate(),
                move.getMovedPiece().getPieceAlliance());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 9);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
}
