/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import pacman.keyactions.GameKA;
import pacman.player.Player;

/**
 *
 * @author Petr
 */
public class GamePanel extends JPanel implements ActionListener {

    public final Timer timer;

    private Timer counterTimer;
    private final Dimension dim;
    private Image maze;
    private Player player;
    private boolean inGame;
    private int countDown;
    private final Font infoFont;
    private final Font counterFont;
    private final Font scoreFont;
    private BufferedImage pacman;

    public Dimension getDim() {
        return dim;
    }

    public Player getPlayer() {
        return player;
    }

    public GamePanel() throws IOException {
        dim = new Dimension(464, 562);
        infoFont = new Font("Arial", Font.PLAIN, 20);
        counterFont = new Font("Arial", Font.BOLD, 100);
        scoreFont = new Font("Helvetica", Font.BOLD, 20);
        loadImages();
        initComponents();
        setPreferredSize(dim);
        setBackground(Color.black);
        setDoubleBuffered(true);
        timer = new Timer(40, this);
        timer.start();
    }

    public void startGame() {
        countDown = 3;
        ActionListener counter = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (--countDown < 1) {
                    inGame = true;
                    counterTimer.stop();
                    player.hold = false;
                }
                repaint();
            }
        };
        counterTimer = new Timer(1000, counter);
        counterTimer.start();
    }

    public boolean isInGame() {
        return inGame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, dim.width, dim.height);
        ((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP
        );
        if (inGame) {
            drawMaze(g);
            drawScore(g);
            drawLives(g);
            player.draw(g);
            //drawGhosts(g);
        } else if (countDown > 0) {
            drawMaze(g);
            drawScore(g);
            drawLives(g);
            player.draw(g);
            drawCount(g);
        } else {
            showIntro(g);
        }
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private void loadImages() throws IOException {
        maze = ImageIO.read(new File(Const.imagePath + "maze.jpg"));
        pacman = ImageIO.read(new File(Const.imagePath + "pacman2.png"));
    }

    private void initComponents() throws IOException {
        addKeyListener(new GameKA(this));
        player = new Player(this);

    }

    private void drawMaze(Graphics g) {
        g.drawImage(maze, 0, 25, this);
    }

    private void drawScore(Graphics g) {
        int numHorAlig = getFontMetrics(counterFont).getDescent();

        g.setColor(Color.white);
        g.setFont(scoreFont);
        g.drawString(String.valueOf(player.getScore()), 5, numHorAlig);
    }

    private void drawLives(Graphics g) {
        for (int i = 0; i < player.getLives(); i++) {
            g.drawImage(pacman, Const.padding + Const.liveSize * i,
                        dim.height - Const.liveSize,
                        Const.liveSize, Const.liveSize, this);
        }
    }

    private void showIntro(Graphics g) {
        g.setColor(Color.white);
        g.setFont(infoFont);
        int strWidth = SwingUtilities.computeStringWidth(
                                        getFontMetrics(infoFont),
                                        Const.introMessage);
        g.drawString(Const.introMessage,
                    (dim.width - strWidth) / 2, dim.height / 2);
    }

    private void drawCount(Graphics g) {
        int numWidth = SwingUtilities.computeStringWidth(
                                        getFontMetrics(counterFont),
                                        String.valueOf(countDown));
        int numHorAlig = getFontMetrics(counterFont).getDescent();
        g.setColor(Color.white);
        g.setFont(counterFont);
        g.drawString(String.valueOf(countDown), (dim.width - numWidth) / 2, (dim.height + numHorAlig) / 2);
    }
}
