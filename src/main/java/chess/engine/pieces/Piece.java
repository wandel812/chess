package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;

import java.util.Collection;


public abstract class Piece {
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    public Piece(final PieceType pieceType,
                 final int piecePosition,
                 final Alliance pieceAlliance,
                 final boolean isFirstMove) {
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;
        cachedHashCode = computeHashCode();

    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public int getPieceValue() {
        return pieceType.getPieceValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if(!(o instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) o;
        return piecePosition == otherPiece.getPiecePosition()
                && pieceType == otherPiece.getPieceType()
                && pieceAlliance == otherPiece.getPieceAlliance()
                && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);
    public abstract Piece movePiece(final Move move);

    public enum PieceType {
        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K", 10000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(String pieceName, int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        public int getPieceValue() {
            return pieceValue;
        }

        @Override
        public String toString() {
            return pieceName;
        }
        public abstract boolean isKing();
        public abstract boolean isRook();
    }
}
