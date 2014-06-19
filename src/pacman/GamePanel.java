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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    private final Dimension dim;
    
    private Image maze;
    
    private Player player;
    
    private boolean inGame;
    
    public final Timer timer;
        
    public GamePanel() throws IOException {
        dim = new Dimension(464, 562);
        loadImages();
        initComponents();
        setPreferredSize(dim);
        setBackground(Color.black);
        setDoubleBuffered(true);
        timer = new Timer(40, this);
        timer.start();
    }
    
    public void startGame(){
        inGame = true;
    }
    
    public boolean isInGame() {
        return inGame;
    }
        
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, dim.width, dim.height);
        if(inGame){
            drawMaze(g);
            //drawScore(g);
            //drawLives(g);
            //player.draw(g);
            //drawGhosts(g);
        }else{
            showIntro(g); 
        }
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        requestFocusInWindow();
    }

    private void loadImages() throws IOException {
        String imagePath = "images/";
        maze = ImageIO.read(new File(imagePath+"maze.jpg"));
    }
    private void initComponents() throws IOException {
        addKeyListener(new GameKA(this));
        player = new Player();
        
    }
    private void drawMaze(Graphics g) {
        g.drawImage(maze, 0, 25, this);
    }

    
    private void drawScore(Graphics g) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void drawLives(Graphics g) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void showIntro(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 20) );
        int stringWidth = SwingUtilities.computeStringWidth(getFontMetrics(getFont()), Const.INTROMESSAGE);
        g.drawString(Const.INTROMESSAGE, (dim.width/2-stringWidth), dim.height/2);
    }
}
