package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {
    private static final int[] CANDIDATE_MOVE_COORDINATE = {8, 16, 7, 9};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            final int candidateDestinationCoordinate = piecePosition
                    + (this.getPieceAlliance().getDirection() * currentCandidateOffset);
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                //TODO more work to do here!!! Deal with promotions
                legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
            } else if (currentCandidateOffset == 16 && this.isFirstMove()
                    && ((BoardUtils.SECOND_ROW[piecePosition] && pieceAlliance.isBlack()))
                    || (BoardUtils.SEVENTH_ROW[piecePosition]) && pieceAlliance.isWhite()) {
                final int behindCandidateDestinationCoordinate = this.piecePosition
                        + (pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
                        && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 7
                    && !((BoardUtils.FIRST_COLUMN[piecePosition] && pieceAlliance.isBlack())
                    || (BoardUtils.EIGHTH_COLUMN[piecePosition] && pieceAlliance.isWhite()))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        //TODO more to do here
                        legalMoves.add(new Move.AttackMove(
                                board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    }
                }

            } else if (currentCandidateOffset == 9
                    && !((BoardUtils.FIRST_COLUMN[piecePosition] && pieceAlliance.isWhite()
                    || BoardUtils.EIGHTH_COLUMN[piecePosition] && pieceAlliance.isBlack()))) {
                final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    if (pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        //TODO more to do here
                        legalMoves.add(new Move.AttackMove(
                                board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}
