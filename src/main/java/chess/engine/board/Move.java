package chess.engine.board;

import chess.engine.board.Board.Builder;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;

import java.lang.annotation.Inherited;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board,
                final Piece movedPiece,
                final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board,
                 final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        movedPiece = null;
        isFirstMove = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + destinationCoordinate;
        result = prime * result + movedPiece.hashCode();
        result = prime * result + movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) o;
        return  getCurrentCoordinate() == otherMove.getCurrentCoordinate()
                && getDestinationCoordinate() == otherMove.getDestinationCoordinate()
                && getMovedPiece().equals(otherMove.getMovedPiece());

    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public int getCurrentCoordinate() {
        return movedPiece.getPiecePosition();
    }

    public int getDestinationCoordinate() {
        return destinationCoordinate;
    }

    public Board execute() {
        final Builder builder = new Builder();
        for (final Piece piece : board.currentPlayer().getActivePieces()) {
            if (!movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }
        for (final Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        // move the moved piece!
        builder.setPiece(movedPiece.movePiece(this));
        builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static class MajorAttackMove extends AttackMove {
        public MajorAttackMove(final Board board,
                               final Piece pieceMoved,
                               final int destinationCoordinate,
                               final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }
    }

    public static final class MajorMove extends Move {
        public MajorMove(final Board board,
                         final Piece movedPiece,
                         final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof MajorMove && super.equals(o);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString()
                    + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) o;
            return super.equals(otherAttackMove)
                    && attackedPiece.equals(otherAttackMove.getAttackedPiece());

        }

        @Override
        public int hashCode() {
            return attackedPiece.hashCode() + super.hashCode();
        }


        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return attackedPiece;
        }
    }

    public static final class PawnMove extends Move {
        public PawnMove(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof  PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(movedPiece.getPiecePosition()).substring(0, 1) + "x"
                    + BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(final Board board,
                                       final Piece movedPiece,
                                       final int destinationCoordinate,
                                       final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : board.currentPlayer().getActivePieces()) {
                if(!movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
                if(!piece.equals(getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(movedPiece.movePiece(this));
            builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static class PawnPromotion extends Move {
        final Move decoratedMove;
        final Pawn promotedPawn;
        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public int hashCode() {
            return  decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard = decoratedMove.execute();
            final Board.Builder builder = new Builder();
            for(final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
                if(!promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
                    builder.setPiece(piece);
            }
            builder.setPiece(promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return "";
        }
    }

    private Board getBoard() {
        return board;
    }

    public static final class PawnJump extends Move {
        public PawnJump(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : board.currentPlayer().getActivePieces()) {
                if (!movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }
    }

    public static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : board.currentPlayer().getActivePieces()) {
                if (!movedPiece.equals(piece) && !castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(movedPiece.movePiece(this));
            //TODO look into the first move on normal pieces
            builder.setPiece(new Rook(castleRookDestination, castleRook.getPieceAlliance()));
            builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + castleRook.hashCode();
            result = prime * result + castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate,
                    castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof KingSideCastleMove &&  super.equals(other);
        }

        @Override
        public String toString() {
            return "O-0";
        }
    }

    public static class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate,
                    castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof KingSideCastleMove &&  super.equals(other);
        }

        @Override
        public String toString() {
            return "O-0-O";
        }
    }

    public static class NullMove extends Move {
        public NullMove() {
            super(null,65);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Can not execute the null move!");
        }

        @Override
        public int  getCurrentCoordinate(){
            return -1;
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Not instantiable!");
        }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMove()) {
                if (move.getCurrentCoordinate() == currentCoordinate
                        && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }

    }

}


