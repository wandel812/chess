package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class BlackPlayer extends Player {
    public BlackPlayer(final Board board,
                       final Collection<Move> blackStandardLegalMoves,
                       final  Collection<Move> whiteStandardLegalMoves) {
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
        return board.getWhitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentsLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (playerKing.isFirstMove() && !isInCheck()) {
            // black's king side castle
            if (!board.getTile(5).isTileOccupied() && !board.getTile(6).isTileOccupied()) {
                final Tile rookTile = board.getTile(7);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (!Player.calculateAttacksOnTile(5, getOpponent().getLegalMoves()).isEmpty()
                            && !Player.calculateAttacksOnTile(6, getOpponent().getLegalMoves()).isEmpty()
                            && rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(
                                board,
                                playerKing,
                                6,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                5
                        ));
                    }
                }
            }
            // black's queen side castle
            if (!board.getTile(1).isTileOccupied()
                    && !board.getTile(2).isTileOccupied()
                    && !board.getTile(3).isTileOccupied()) {
                final Tile rookTile = board.getTile(0);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (!Player.calculateAttacksOnTile(1, getOpponent().getLegalMoves()).isEmpty()
                            && !Player.calculateAttacksOnTile(2, getOpponent().getLegalMoves()).isEmpty()
                            && !Player.calculateAttacksOnTile(3, getOpponent().getLegalMoves()).isEmpty()
                            && rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenSideCastleMove(
                                board,
                                playerKing,
                                2,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                3
                        ));
                    }
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
}
