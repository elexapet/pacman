/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.player;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import pacman.Const;
import pacman.Const.Direction;
import pacman.GamePanel;

/**
 *
 * @author Petr
 */
public class Player {

    public Direction dir;
    public boolean hold;
    
    private int lives;
    private int score;
    private class Pacman{
        BufferedImage left;
        BufferedImage right;
        BufferedImage up;
        BufferedImage down;
    };
    private final Pacman pacman1;
    private final Pacman pacman2;
    private BufferedImage pacman3;
    private int positionX;
    private int positionY;
    private final GamePanel parent;
    private int anim;
    private int count;

    private void loadImages() throws IOException {
        String imagePath = "images/";
        pacman1.right = ImageIO.read(new File(imagePath + "pacman1.png"));
        pacman1.left = rotate(pacman1.right, 180);
        pacman1.up = rotate(pacman1.right, 270);
        pacman1.down = rotate(pacman1.right, 90);
        pacman2.right = ImageIO.read(new File(imagePath + "pacman2.png"));
        pacman2.left = rotate(pacman2.right, 180);
        pacman2.up = rotate(pacman2.right, 270);
        pacman2.down = rotate(pacman2.right, 90);
        pacman3 = ImageIO.read(new File(imagePath + "pacman3.png"));
        
    }
    
    private BufferedImage rotate(BufferedImage image, double angle){
        double rad = Math.toRadians(angle);
        AffineTransform transform = new AffineTransform();
        transform.rotate(rad, image.getWidth()/2, image.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }
    
    private BufferedImage getImageForDirection(Pacman pacman){
        switch(dir){
            case LEFT:
                return pacman.left;
            case RIGHT:
                return pacman.right;
            case UP:
                return pacman.up;
            case DOWN:
                return pacman.down;
        }
        return null;
    }

    public Player(GamePanel parent) throws IOException {
        this.parent = parent;
        pacman1 = new Pacman();
        pacman2 = new Pacman();
        loadImages();
        lives = 3;
        score = 0;
        anim = 1;
        count = 0;
        hold = true;
        dir = Direction.LEFT;
        positionX = (parent.getDim().width-25)/2;
        positionY = 384+25-25/2;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public void draw(Graphics g) {
        if (!hold) {
            if ((++count % Const.animDelay) == 0) {
                anim = ++anim % 3;
            }
            if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                positionX += Const.speed * dir.value();
            } else {
                positionY += Const.speed * dir.value();
            }
        }
        switch (anim) {
            case 0:
                g.drawImage(getImageForDirection(pacman1),
                            positionX, positionY, 25, 25, parent);
                break;
            case 1:
                g.drawImage(getImageForDirection(pacman2),
                            positionX, positionY, 25, 25, parent);
                break;
            case 2:
                g.drawImage(pacman3, positionX, positionY, 25, 25, parent);
                break;
        }
        
    }

}
