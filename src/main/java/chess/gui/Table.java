package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;

    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(500, 500);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(300, 250);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(7, 7);

    private final static Color lightTileColor = Color.decode("#FFFACD");
    private final static Color darkTileColor = Color.decode("#593E1A");
    private static String defaultPieceImagesPath = "src/main/resources/pieceIcon/";
    private boolean highlightLegalMoves = false;

    public Table() {
        gameFrame = new JFrame("Chess");
        gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setJMenuBar(tableMenuBar);
        gameFrame.setSize(OUTER_FRAME_DIMENSION);
        chessBoard = Board.createStandardBoard();
        gameHistoryPanel = new GameHistoryPanel();
        takenPiecesPanel = new TakenPiecesPanel();
        boardPanel = new BoardPanel();
        moveLog = new MoveLog();
        boardDirection = BoardDirection.NORMAL;
        gameFrame.add(takenPiecesPanel, BorderLayout.WEST);
        gameFrame.add(boardPanel, BorderLayout.CENTER);
        gameFrame.add(gameHistoryPanel, BorderLayout.EAST);
        gameFrame.setVisible(true);

    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
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

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip board");

        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMovesHighlighterCheckbox = new JCheckBoxMenuItem("Highlight legal moves", false);
        legalMovesHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                highlightLegalMoves = legalMovesHighlighterCheckbox.isSelected();
            }
        });

        preferencesMenu.add(legalMovesHighlighterCheckbox);

        return preferencesMenu;


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

        public void drawBoard(final Board chessBoard) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(chessBoard);
                add(tilePanel);
            }
            validate();
            repaint();
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
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;

                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            // second click
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(
                                    chessBoard,
                                    sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);

                            }
                        });
                    }
                }
            });
            validate();
        }

        private void assignTilePieceIcon(final Board board) {
            removeAll();
            if (board.getTile(tileId).isTileOccupied()) {
                String suffixPath
                        =  board.getTile(tileId).getPiece().getPieceAlliance().toString().substring(0, 1)
                        + board.getTile(tileId).getPiece().toString()
                        + ".gif";
                try {
                    final BufferedImage image
                            = ImageIO.read(new File(defaultPieceImagesPath + suffixPath));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void highlightLegals(final Board board) {
            if(highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if(move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("src/main/resources/misc/green_dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
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

        public void drawTile(final Board chessBoard) {
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            highlightLegals(chessBoard);
            validate();
            repaint();

        }
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    public static class MoveLog {
        private final List<Move> moves;

        private MoveLog() {
            moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return moves;
        }

        public void addMove(final Move move) {
            moves.add(move);
        }

        public int size() {
            return moves.size();
        }

        public void clear() {
            moves.clear();
        }

        public Move removeMove(final int index) {
            return moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return moves.remove(move);
        }



    }
}
