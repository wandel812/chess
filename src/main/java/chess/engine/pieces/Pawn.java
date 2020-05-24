package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class Pawn extends Piece {
    private static final int[] CANDIDATE_MOVE_COORDINATE = {8, 16, 7, 9};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Alliance getPieceAlliance() {
        return super.getPieceAlliance();
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
                if(pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this,candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 16 && this.isFirstMove()
                    && ((BoardUtils.SEVENTH_RANK[piecePosition] && pieceAlliance.isBlack())
                    || (BoardUtils.SECOND_RANK[piecePosition]) && pieceAlliance.isWhite())) {
                final int behindCandidateDestinationCoordinate = this.piecePosition
                        + (pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
                        && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 7
                    && !((BoardUtils.FIRST_COLUMN[piecePosition] && pieceAlliance.isBlack())
                    || (BoardUtils.EIGHTH_COLUMN[piecePosition] && pieceAlliance.isWhite()))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        if(pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board,
                                            this,
                                            candidateDestinationCoordinate,
                                            pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnAttackMove(
                                    board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if(board.getEnPassantPawn() != null) {
                    if(board.getEnPassantPawn().piecePosition
                            == (piecePosition + (pieceAlliance.getOppositeDirection()))) {
                        final Pawn pieceOnCandidate = board.getEnPassantPawn();
                        if(pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttackMove(
                                    board,
                                    this,
                                    candidateDestinationCoordinate,
                                    pieceOnCandidate));
                        }
                    }
                }
            } else if (currentCandidateOffset == 9
                    && !((BoardUtils.FIRST_COLUMN[piecePosition] && pieceAlliance.isWhite()
                    || BoardUtils.EIGHTH_COLUMN[piecePosition] && pieceAlliance.isBlack()))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        if(pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board,
                                            this,
                                            candidateDestinationCoordinate,
                                            pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnAttackMove(
                                    board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if(board.getEnPassantPawn() != null) {
                    if(board.getEnPassantPawn().piecePosition
                            == (piecePosition - (pieceAlliance.getOppositeDirection()))) {
                        final Pawn pieceOnCandidate = board.getEnPassantPawn();
                        if(pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttackMove(
                                    board,
                                    this,
                                    candidateDestinationCoordinate,
                                    pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(),
                move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece() {
        return new Queen(piecePosition, pieceAlliance, false);
    }
}
