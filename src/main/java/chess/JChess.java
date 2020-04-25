package chess;

import chess.engine.board.Board;
import chess.gui.Table;

public class JChess {
    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        Table table = new Table();
        System.out.println(board);
    }
}
