/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.ghosts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
 public abstract class AbstractGhost {

    private Const.Direction dir;
    private Const.Direction nextDir;
    private final BufferedImage ghostImage;
    private final GamePanel parent;
    private int absoluteX;
    private int absoluteY;
    private int relativeX;
    private int relativeY;

    public AbstractGhost(GamePanel parent) throws IOException {
        this.parent = parent;
        ghostImage = ImageIO.read(new File(Const.imagePath + "red.png"));
        relativeX = 14;
        relativeY = 14;
        absoluteX = parent.absolutePositionX(relativeX);
        absoluteY = parent.absolutePositionY(relativeY);
    }

    public void draw(Graphics g) {
        g.drawImage(ghostImage, absoluteX - Const.iconSize / 2,
                absoluteY - Const.iconSize / 2, Const.iconSize,
                Const.iconSize, parent);
        move();
        relativeX = parent.relativePositionX(absoluteX);
        relativeY = parent.relativePositionY(absoluteY);
    }

    private void move() {

        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            if (isPlanningPoint(relativeX + dir.value(), relativeY)) {
                planNextTurn();
            }
            if (isTurningPointX()) {
                if (isPlanningPoint(relativeX, relativeY)) {
                    dir = nextDir;
                }
                if (isOneWayTurn()) {
                    if (canGoUp()) {
                        dir = dir.UP;
                    } else {
                        dir = dir.DOWN;
                    }
                }
                move();
            } else {
                absoluteX += dir.value();
            }
        } else if (dir == Direction.UP || dir == Direction.DOWN) {
            if (isPlanningPoint(relativeX, relativeY + dir.value())) {
                planNextTurn();
            }
            if (isDecisionPointY()) {
                if (isPlanningPoint(relativeX, relativeY)) {
                    dir = nextDir;
                }
                if (isOneWayTurn()) {
                    if (canGoLeft()) {
                        dir = dir.LEFT;
                    } else {
                        dir = dir.RIGHT;
                    }
                }
                move();
            } else {
                absoluteY += dir.value();
            }
        }
    }

    private boolean isTurningPointX() {
        return absoluteX == parent.absolutePositionX(relativeX);
    }

    private boolean isDecisionPointY() {
        return absoluteY == parent.absolutePositionY(relativeY);
    }

    private boolean isPlanningPoint(int x, int y) {
        return (parent.mazeGrid[y][x] & 15) <= 8;
    }

    private boolean isOneWayTurn() {
        return ((parent.mazeGrid[relativeY][relativeX] & 15) % 3) == 0;
    }

    private boolean canGoUp() {
        return (parent.mazeGrid[relativeY][relativeX] & 2) != 2;
    }

    private boolean canGoLeft() {
        return (parent.mazeGrid[relativeY][relativeX] & 1) != 1;
    }

    abstract void planNextTurn();

}
