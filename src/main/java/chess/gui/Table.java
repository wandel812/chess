package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Table {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 355);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private final static Color lightTileColor = Color.decode("#FFFACD");
    private final static Color darkTileColor = Color.decode("#593E1A");

    public Table() {
        gameFrame = new JFrame("Chess");
        gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setJMenuBar(tableMenuBar);
        gameFrame.setSize(OUTER_FRAME_DIMENSION);
        boardPanel = new BoardPanel();
        gameFrame.add(boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true);

    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN file");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("open up that .pgn file!");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private class BoardPanel extends JPanel {
        final private List<TilePanel> boardTiles;

        public BoardPanel() {
            super(new GridLayout(8, 8));
            boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

    }

    private class TilePanel extends JPanel {
        private final int tileId;
        public TilePanel(final BoardPanel boardPanel,
                         final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            validate();
        }



        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[tileId]
                    || BoardUtils.SIXTH_RANK[tileId]
                    || BoardUtils.FOURTH_RANK[tileId]
                    || BoardUtils.SECOND_RANK[tileId]) {
                setBackground(tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[tileId]
                    || BoardUtils.FIFTH_RANK[tileId]
                    || BoardUtils.THIRD_RANK[tileId]
                    || BoardUtils.FIRST_RANK[tileId]) {
                setBackground(tileId % 2 == 0 ? darkTileColor : lightTileColor );
            }

        }
    }
}
