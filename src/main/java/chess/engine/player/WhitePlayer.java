package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Move.KingSideCastleMove;
import chess.engine.board.Move.QueenSideCastleMove;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {
    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return board.getBlackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentsLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (playerKing.isFirstMove() && !isInCheck()) {
            // white's king side castle
            if (!board.getTile(61).isTileOccupied() && !board.getTile(62).isTileOccupied()) {
                final Tile rookTile = board.getTile(63);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (!Player.calculateAttacksOnTile(61, getOpponent().getLegalMoves()).isEmpty()
                            && !Player.calculateAttacksOnTile(62, getOpponent().getLegalMoves()).isEmpty()
                            && rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(
                                board,
                                playerKing,
                                62,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                61
                        ));
                    }
                }
            }
            // white's queen side castle
            if (!board.getTile(59).isTileOccupied()
                    && !board.getTile(58).isTileOccupied()
                    && !board.getTile(57).isTileOccupied()) {
                final Tile rookTile = board.getTile(56);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (!Player.calculateAttacksOnTile(59, getOpponent().getLegalMoves()).isEmpty()
                            && !Player.calculateAttacksOnTile(58, getOpponent().getLegalMoves()).isEmpty()
                            && !Player.calculateAttacksOnTile(57, getOpponent().getLegalMoves()).isEmpty()
                            && rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenSideCastleMove(
                                board,
                                playerKing,
                                58,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                59
                        ));
                    }
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
}
