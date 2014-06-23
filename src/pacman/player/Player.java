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
    private boolean holdAnim;

    private class Pacman {

        BufferedImage left;
        BufferedImage right;
        BufferedImage up;
        BufferedImage down;
    };
    private final Pacman pacman1;
    private final Pacman pacman2;
    private BufferedImage pacman3;
    private int lives;
    private int score;
    private int absoluteX;
    private int absoluteY;
    private int relativeX;
    private int relativeY;
    private final GamePanel parent;
    private int anim;
    private int count;

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
        holdAnim = false;
        dir = Direction.LEFT;
        relativeX = 14;
        relativeY = 23;
        absoluteX = parent.absolutePositionX(relativeX);
        absoluteY = parent.absolutePositionY(relativeY);
        System.out.println(parent.relativePositionX(0));
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public void draw(Graphics g) {
        move();
        switch (anim) {
            case 0:
                g.drawImage(getImageForDirection(pacman1),
                        absoluteX - Const.iconSize / 2,
                        absoluteY - Const.iconSize / 2,
                        Const.iconSize, Const.iconSize, parent);
                break;
            case 1:
                g.drawImage(getImageForDirection(pacman2),
                        absoluteX - Const.iconSize / 2,
                        absoluteY - Const.iconSize / 2,
                        Const.iconSize, Const.iconSize, parent);
                break;
            case 2:
                g.drawImage(pacman3, absoluteX - Const.iconSize / 2,
                        absoluteY - Const.iconSize / 2,
                        Const.iconSize, Const.iconSize, parent);
                break;
        }

    }

    private void move() {
        int oldRelativeX = relativeX;
        int oldRelativeY = relativeY;
        if (!hold) {
            if ( !holdAnim &&(++count % Const.animDelay) == 0) {
                anim = ++anim % 3;
            }
            if ((parent.mazeGrid[relativeY][relativeX] & 16) == 16) {
                score += Const.pointValue;
                parent.mazeGrid[relativeY][relativeX] -= 16;
            }
            if (dir == Direction.LEFT && ((parent.mazeGrid[relativeY][relativeX] & 1) != 1)) {
                absoluteX += Const.baseSpeed * dir.value();
                absoluteX = (parent.getWidth() + absoluteX) % parent.getWidth();
                holdAnim = false;
            } else if (dir == Direction.RIGHT && ((parent.mazeGrid[relativeY][relativeX] & 4) != 4)) {
                absoluteX += Const.baseSpeed * dir.value();
                absoluteX = (parent.getWidth() + absoluteX) % parent.getWidth();
                holdAnim = false;
            } else if (dir == Direction.UP && ((parent.mazeGrid[relativeY][relativeX] & 2) != 2)) {
                absoluteY += Const.baseSpeed * dir.value();
                absoluteY = (parent.getHeight() + absoluteY) % parent.getHeight();
                holdAnim = false;
            } else if (dir == Direction.DOWN && ((parent.mazeGrid[relativeY][relativeX] & 8) != 8)) {
                absoluteY += Const.baseSpeed * dir.value();
                absoluteY = (parent.getHeight() + absoluteY) % parent.getHeight();
                holdAnim = false;
            } else {
                holdAnim = true;
            }
            relativeX = (Const.gridWidth + parent.relativePositionX(absoluteX)) % Const.gridWidth;
            relativeY = (Const.gridHeight + parent.relativePositionY(absoluteY)) % Const.gridHeight;
        }
    }

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

    private BufferedImage rotate(BufferedImage image, double angle) {
        double rad = Math.toRadians(angle);
        AffineTransform transform = new AffineTransform();
        transform.rotate(rad, image.getWidth() / 2, image.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }

    private BufferedImage getImageForDirection(Pacman pacman) {
        switch (dir) {
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
}
