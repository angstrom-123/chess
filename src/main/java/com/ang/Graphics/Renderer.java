package com.ang.Graphics;

import com.ang.Pieces.PieceType;
import com.ang.Util.InputHandler;
import com.ang.Pieces.PieceColour;
import com.ang.GameInterface;
import com.ang.Pieces.Piece;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Renderer extends JFrame{
    final Colour DARK_COL = new Colour(112,102,119);
    final Colour LIGHT_COL = new Colour(204,183,174);
    final Colour HIGHLIGHT_COL = new Colour(255,106,60);
    final int SQUARE_SIZE = 45;

    private double scale;
    private int size;

    private BufferedImage img;
    private ImagePanel imgPanel;
    private JFrame frame;
    private GameInterface gameInterface;

    public Renderer(double scale, GameInterface gameInterface) {
        this.scale = scale;
        this.gameInterface = gameInterface;

        init();
        drawBoard();
    }

    public void init() {
        size = SQUARE_SIZE * 8;
        Dimension paneDimension = new Dimension((int)Math.round(size * scale),
                                                (int)Math.round(size * scale));

        img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        imgPanel = new ImagePanel(img);

        frame = new JFrame();
        frame.getContentPane().setPreferredSize(paneDimension);
        frame.getContentPane().add(imgPanel);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                frame.dispose();
            }
        });

        imgPanel.addMouseListener(new InputHandler(gameInterface));

        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    public void drawBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if ((y + x) % 2 == 0) {
                    drawSquare(x, y, LIGHT_COL);
                } else {
                    drawSquare(x, y, DARK_COL);
                }
            }
        }  
        frame.repaint();
    }

    private void drawSquare(int x, int y, Colour col) {
        int startX = x * SQUARE_SIZE;
        int startY = y * SQUARE_SIZE;
        for (int i = startX; i < startX + SQUARE_SIZE; i++) {
            for (int j = startY; j < startY + SQUARE_SIZE; j++) {
                drawPixel(i, j, col);
            }
        }
    }

    public void drawAllSprites(Piece[] board) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board[y * 8 + x];
                if (p.type() != PieceType.NONE) {
                    drawSprite(x, y, p.type(), p.colour());
                }
            }
        }
    }

    private void drawSprite(int x, int y, PieceType type, PieceColour col) {
        int startX = x * SQUARE_SIZE;
        int startY = y * SQUARE_SIZE;

        Colour tint;
        switch (col) {
            case WHITE:
                tint = new Colour(255, 255, 255);
                break;
            case BLACK:
                tint = new Colour(0, 0, 0);
                break;
            default:
                tint = new Colour(127, 127, 127);
                break;
        }

        BufferedImage s = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE,
                                            BufferedImage.TYPE_INT_RGB);
        try {
            s = ImageIO.read(this.getClass().getResource(type.path()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int j = 0; j < SQUARE_SIZE; j++) {
            for (int i = 0; i < SQUARE_SIZE; i++) {
                int samp = s.getRGB(i, j);

                if ((samp & 0xff000000 >> 24) == 0) {
                    continue;
                }

                if ((samp & 0x00ffffff) == 0) {
                    Colour pixelCol = new Colour(0, 0, 0);
                    drawPixel(startX + i, startY + j, pixelCol);
                    continue;
                } 
                int r = samp & 0x00ff0000 >> 16;
                int g = samp & 0x0000ff00 >> 8;
                int b = samp & 0x000000ff;

                r = (int)Math.round((r + tint.r()) / 2);
                g = (int)Math.round((g + tint.g()) / 2);
                b = (int)Math.round((b + tint.b()) / 2);

                Colour pixelCol = new Colour(r, g, b);
                drawPixel(startX + i, startY + j, pixelCol);
            }
        }

        frame.repaint();
    }

    private void drawPixel(int x, int y, Colour col) {
        int r = (int)Math.round(col.r());
        int g = (int)Math.round(col.g());
        int b = (int)Math.round(col.b());
        int pixelCol = (r << 16) | (g << 8) | (b);

        img.setRGB(x, y, pixelCol);
    }
}
   