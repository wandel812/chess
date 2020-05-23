package chess.gui;

import chess.engine.board.Move;
import chess.engine.pieces.Piece;
import chess.gui.Table.MoveLog;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TakenPiecesPanel extends JPanel {
    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        northPanel = new JPanel(new GridLayout(8, 2));
        southPanel = new JPanel(new GridLayout(8, 2));

        northPanel.setBackground(PANEL_COLOR);
        southPanel.setBackground(PANEL_COLOR);
        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog moveLog) {
        northPanel.removeAll();
        southPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move : moveLog.getMoves()) {
            if(move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else if(takenPiece.getPieceAlliance().isBlack()){
                    blackTakenPieces.add(takenPiece);
                } else {
                    throw new RuntimeException("should not reach here!!!");
                }

                Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
                    @Override
                    public int compare(Piece piece, Piece t1) {
                        return Ints.compare(piece.getPieceValue(), t1.getPieceValue());
                    }
                });

                Collections.sort(blackTakenPieces, new Comparator<Piece>() {
                    @Override
                    public int compare(Piece piece, Piece t1) {
                        return Ints.compare(piece.getPieceValue(), t1.getPieceValue());
                    }
                });

                for(final Piece whiteTakenPiece : whiteTakenPieces) {
                    try {
                        final BufferedImage image = ImageIO.read(new File("src/main/resources/pieceIcon/"
                                + whiteTakenPiece.getPieceAlliance().toString().substring(0, 1) + ""
                                + whiteTakenPiece.toString()));
                        final ImageIcon icon = new ImageIcon(image);
                        final JLabel imageLabel = new JLabel();
                        southPanel.add(imageLabel);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }

                for(final Piece blackTakenPiece : blackTakenPieces) {
                    try {
                        final BufferedImage image = ImageIO.read(new File("src/main/resources/pieceIcon/"
                                + blackTakenPiece.getPieceAlliance().toString().substring(0, 1) + ""
                                + blackTakenPiece.toString()));
                        final ImageIcon icon = new ImageIcon(image);
                        final JLabel imageLabel = new JLabel();
                        southPanel.add(imageLabel);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }

                validate();
            }
        }


    }


}
