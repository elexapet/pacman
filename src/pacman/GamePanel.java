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
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import pacman.ghosts.AbstractGhost;
import pacman.ghosts.Blue;
import pacman.ghosts.Orange;
import pacman.ghosts.Pink;
import pacman.ghosts.Red;
import pacman.keyactions.GameKA;
import pacman.player.Player;

/**
 * Hlavní herní plocha řídí chod samotné hry
 *
 * @author Petr
 */
public class GamePanel extends JPanel implements ActionListener {

    public final Timer timer;
    public List<byte[]> mazeGrid;

    private Timer counterTimer;
    private Image mazeBackground;
    private Player player;
    private boolean inGame;
    private int countDownNum;
    private BufferedImage pacman;
    private final Dimension dim;
    private final Font introFont;
    private final Font counterFont;
    private final Font scoreFont;
    private boolean scatter;
    private long time;
    private int phase;
    private AbstractGhost redGhost;
    private AbstractGhost pinkGhost;
    private AbstractGhost blueGhost;
    private AbstractGhost orangeGhost;
    private boolean win;

    /**
     * konstruktor volá inicializační metody a inicializuje proměnné
     *
     * @throws IOException
     */
    public GamePanel() throws IOException {
        dim = new Dimension(464, 562);
        introFont = new Font("Arial", Font.PLAIN, 20);
        counterFont = new Font("Arial", Font.BOLD, 100);
        scoreFont = new Font("Helvetica", Font.BOLD, 20);
        win = false;
        loadImages();
        initComponents();
        setPreferredSize(dim);
        setBackground(Color.black);
        setDoubleBuffered(true);
        mazeGrid = Arrays.asList(Const.MazeGridData);
        timer = new Timer(Const.renderingSpeed, this);
        timer.start();
    }

    @Override
    public int getHeight() {
        return dim.height;
    }

    @Override
    public int getWidth() {
        return dim.width;
    }

    /**
     *
     * @return jestli se nachází ve scatter módu
     */
    public boolean isScatter() {
        return scatter;
    }

    /**
     *
     * @return objekt hráče
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * voláno hráčem pro kontrolu kolize s duchem
     *
     * @param relX horizontální pozice hráče
     * @param relY vertikální pozice hráče
     * @return hráč byl chycen
     */
    public boolean gotCaught(int relX, int relY) {
        return (relX == redGhost.getRelativeX() && relY == redGhost.getRelativeY())
                || (relX == pinkGhost.getRelativeX() && relY == pinkGhost.getRelativeY())
                || (relX == blueGhost.getRelativeX() && relY == blueGhost.getRelativeY())
                || (relX == orangeGhost.getRelativeX() && relY == orangeGhost.getRelativeY());
    }

    /**
     *
     * @param x absolutní horizontální pozice
     * @return relativní horizontální pozice
     */
    public int relativePositionX(int x) {
        return (int) Math.round((double) (x + Const.gridElemSize / 2
                - Const.gridOffset) / Const.gridElemSize) - 1;
    }

    /**
     *
     * @param y absolutní vertikální pozice
     * @return relativní vertikální pozice
     */
    public int relativePositionY(int y) {
        return (int) Math.round((double) (y + Const.gridElemSize / 2
                - Const.gridOffset - Const.mazeOffset) / Const.gridElemSize) - 1;
    }

    /**
     *
     * @param x relativní horizontální pozice
     * @return absolutní horizontální pozice
     */
    public int absolutePositionX(int x) {
        return Const.gridOffset
                + +Const.gridElemSize * (x + 1) - Const.gridElemSize / 2;
    }

    /**
     *
     * @param y relativní vertikální pozice
     * @return absolutní vertikální pozice
     */
    public int absolutePositionY(int y) {
        return Const.gridOffset + Const.mazeOffset
                + Const.gridElemSize * (y + 1) - Const.gridElemSize / 2;
    }

    /**
     * nutné akce při úmrtí hráče
     */
    public void death() {
        resetGhosts();
    }

    /**
     * resetuje některé proměnné a po 3s odstartuje hru
     */
    public void startGame() {
        
        player.reset();
        resetGhosts();
        win = false;
        countDown();
    }

    /**
     * zastaví hru a počká 1s než skočí do úvodní obrazovky
     */
    public void stopGame() {
        inGame = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        refreshPoints();
    }

    /**
     *
     * @return true pokud se nacházíme ve hře
     */
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
            modeRefresh();
            drawMaze(g);
            drawScore(g);
            drawInfo(g);
            drawLives(g);
            drawGhosts(g);
            player.draw(g);
            checkWin();
        } else if (countDownNum > 0) {
            drawMaze(g);
            drawScore(g);
            drawLives(g);
            player.draw(g);
            drawCount(g);
        } else if (win) {
            showIntro(g, "Vyhrál jsi!");
            drawScore(g);
        } else {
            showIntro(g, Const.introMessage);
        }
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private void resetGhosts() {
        time = 0;
        phase = 1;
        scatter = true;
        redGhost.reset();
        blueGhost.reset();
        orangeGhost.reset();
        pinkGhost.reset();
    }

    private void loadImages() throws IOException {
        mazeBackground = ImageIO.read(new File(Const.imagePath + "maze.jpg"));
        pacman = ImageIO.read(new File(Const.imagePath + "pacman2.png"));
    }

    private void initComponents() throws IOException {
        addKeyListener(new GameKA(this));
        player = new Player(this);
        redGhost = new Red(this);
        pinkGhost = new Pink(this);
        blueGhost = new Blue(this, redGhost);
        orangeGhost = new Orange(this);
    }

    private void drawMaze(Graphics g) {
        g.drawImage(mazeBackground, 0, Const.mazeOffset, this);
        for (int i = 0; i < Const.gridHeight; i++) {
            for (int j = 0; j < Const.gridWidth; j++) {
                if ((mazeGrid.get(i)[j] & 16) == 16) {
                    g.setColor(Color.orange);
                    g.drawOval(absolutePositionX(j), absolutePositionY(i), 4, 4);
                }
            }
        }
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

    private void showIntro(Graphics g, String str) {
        g.setColor(Color.white);
        g.setFont(introFont);
        int strWidth = SwingUtilities.computeStringWidth(
                getFontMetrics(introFont),
                str);
        g.drawString(str,
                (dim.width - strWidth) / 2, dim.height / 2);
    }

    private void drawCount(Graphics g) {
        int numWidth = SwingUtilities.computeStringWidth(
                getFontMetrics(counterFont),
                String.valueOf(countDownNum));
        int numHorAlig = getFontMetrics(counterFont).getDescent();
        g.setColor(Color.white);
        g.setFont(counterFont);
        g.drawString(String.valueOf(countDownNum),
                (dim.width - numWidth) / 2,
                (dim.height + numHorAlig) / 2);
    }

    private void drawGhosts(Graphics g) {
        redGhost.draw(g);
        pinkGhost.draw(g);
        blueGhost.draw(g);
        orangeGhost.draw(g);
    }

    private void modeRefresh() {
        if (phase == 5) {
            return;
        }
        time += Const.renderingSpeed;
        if (phase == 1 && time == 7000) {
            time = 0;
            scatter = false;
            flipGhostDirection();
        } else if (phase == 1 && time == 20000) {
            time = 0;
            phase = 2;
            scatter = true;
            flipGhostDirection();
        } else if (phase == 2 && time == 7000) {
            time = 0;
            scatter = false;
            flipGhostDirection();
        } else if (phase == 2 && time == 20000) {
            time = 0;
            phase = 3;
            scatter = true;
            flipGhostDirection();
        } else if (phase == 3 && time == 5000) {
            time = 0;
            scatter = false;
            flipGhostDirection();
        } else if (phase == 3 && time == 20000) {
            time = 0;
            scatter = true;
            flipGhostDirection();
        } else if (phase == 4 && time == 5000) {
            time = 0;
            phase = 5;
            scatter = false;
            flipGhostDirection();
        }
    }

    private void drawInfo(Graphics g) {
        int horAlig = getFontMetrics(introFont).getAscent();
        int strWidth = SwingUtilities.computeStringWidth(
                getFontMetrics(introFont),
                String.valueOf("SCATTER"));
        g.setColor(Color.white);
        g.setFont(introFont);
        if (scatter) {
            g.drawString("SCATTER", dim.width - (strWidth + 5), horAlig);
        } else {
            g.drawString("CHASE", dim.width - (strWidth + 5), horAlig);
        }
    }

    private void flipGhostDirection() {
        redGhost.flipDirecition();
        pinkGhost.flipDirecition();
        blueGhost.flipDirecition();
        orangeGhost.flipDirecition();
    }

    private void checkWin() {
        for (int i = 0; i < Const.gridHeight; i++) {
            for (int j = 0; j < Const.gridWidth; j++) {
                if ((mazeGrid.get(i)[j] & 16) == 16) {
                    return;
                }
            }
        }
        win = true;
        stopGame();
    }

    private void refreshPoints(){
        for (int i = 0; i < Const.gridHeight; i++) {
            for (int j = 0; j < Const.gridWidth; j++) {
                if ((mazeGrid.get(i)[j] & 32) == 32) {
                    mazeGrid.get(i)[j] |= 16;
                }
            }
        }
    }
    
    private void countDown() {
        countDownNum = 3;
        ActionListener counter = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (--countDownNum < 1) {
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

}
