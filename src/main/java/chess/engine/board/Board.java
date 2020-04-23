package chess.engine.board;

import chess.engine.Alliance;
import chess.engine.pieces.*;
import chess.engine.player.BlackPlayer;
import chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;

import java.util.*;

public class Board {
    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;

     private Board(Builder builder) {
         gameBoard = createGameBoard(builder);
         whitePieces = calculateActivePieces(gameBoard, Alliance.WHITE);
         blackPieces = calculateActivePieces(gameBoard, Alliance.BLACK);
         final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(whitePieces);
         final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(blackPieces);
         whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
         blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
     }

    @Override
    public String toString() {
         final StringBuilder sb = new StringBuilder();
         for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
             final String tileText = this.gameBoard.get(i).toString();
             sb.append(String.format("%3s", tileText));
             if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) {
                 sb.append("\n");
             }
         }
         return sb.toString();
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
         final List<Move> legalMoves = new ArrayList<>();
         for (Piece piece : pieces) {
             legalMoves.addAll(piece.calculateLegalMoves(this));
         }
         return ImmutableList.copyOf(legalMoves);
    }

    private static Collection<Piece> calculateActivePieces(List<Tile> gameBoard, Alliance alliance) {
         final List<Piece> activePieces = new ArrayList<>();
         for (final Tile tile : gameBoard) {
             if (tile.isTileOccupied()) {
                 Piece piece = tile.getPiece();
                 if (piece.getPieceAlliance() == alliance) {
                     activePieces.add(tile.getPiece());
                 }
             }
         }
         return ImmutableList.copyOf(activePieces);
    }

    public Tile getTile(int coordinate) {
        return gameBoard.get(coordinate);
    }

    public static List<Tile> createGameBoard(Builder builder) {
         final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
         for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
             tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
         }
         return ImmutableList.copyOf(tiles);
    }

    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        // Black layout
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        for (int i = 8; i < 16; i++) {
            builder.setPiece(new Pawn(i, Alliance.BLACK));
        }
        // White layout
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));
        for (int i = 48; i < 56; i++) {
            builder.setPiece(new Pawn(i, Alliance.WHITE));
        }
        // White to move
        builder.setNextMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    public Collection<Piece> getBlackPieces() {
         return blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
         return whitePieces;
    }

    public static class Builder {

         Map<Integer, Piece> boardConfig;
         Alliance nextMoveMaker;

         public Builder() {
             boardConfig = new HashMap<>();
         }

         public Builder setPiece(final Piece piece) {
             boardConfig.put(piece.getPiecePosition(), piece);
             return this;
         }

         public Builder setNextMoveMaker(Alliance nextMoveMaker) {
             this.nextMoveMaker = nextMoveMaker;
             return this;
         }


        public Board build() {
            return new Board(this);
        }
    }
}
