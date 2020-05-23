package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.gui.Table.MoveLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100,400);

    public GameHistoryPanel() {
        setLayout(new BorderLayout());
        model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    void redo(final Board board, final MoveLog moveHistory) {
        int currentRow = 0;
        model.clear();
        for(final Move move : moveHistory.getMoves()) {
            final String moveText = move.toString();
            if(move.getMovedPiece().getPieceAlliance().isWhite()) {
                model.setValueAt(moveText, currentRow, 0);
            } else if(move.getMovedPiece().getPieceAlliance().isBlack()) {
                model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if(moveHistory.size() > 0) {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceAlliance().isWhite()) {
                model.setValueAt(moveText + calculateCheckAndCheckMAteHash(board), currentRow, 0);
            } else if(lastMove.getMovedPiece().getPieceAlliance().isBlack()) {
                model.setValueAt(moveText + calculateCheckAndCheckMAteHash(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    private String calculateCheckAndCheckMAteHash(Board board) {
        if(board.currentPlayer().isInCheckMate()) {
            return "#";
        } else if(board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel {
        private static List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            values = new ArrayList<>();
        }

        public void clear() {
            values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if(values == null) {
                return 0;
            }
            return values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = values.get(row);
            if(column == 0) {
                return currentRow.getWhiteMove();
            } else if(column == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object aValue, final int row, final int column) {
            final Row currentRow;
            if(values.size() <= row) {
                currentRow = new Row();
                values.add(currentRow);
            } else {
                currentRow = values.get(row);
            }
            if(column == 0) {
                currentRow.setWhiteMove((String)aValue);
                fireTableRowsInserted(row, row);
            } else if(column == 1) {
                currentRow.setBlackMove((String)aValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int column) {
            return NAMES[column];
        }
    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row() {}

        public String getWhiteMove() {
            return whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setWhiteMove(final String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public void setBlackMove(final String blackMove) {
            this.blackMove = blackMove;
        }






    }
}
