package chess;

import chess.engine.board.Board;
import chess.gui.Table;

public class JChess {
    public static void main(String[] args) {
        System.out.println(Table.PieceImagesDirResourceName);
        System.out.println(Table.HighlightImageDirResourceName);
        Board board = Board.createStandardBoard();
        Table table = new Table();
        System.out.println(board);
    }
}
