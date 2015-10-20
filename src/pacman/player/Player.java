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
 * Třída reprezentující objekt hráče stará se o jeho vykreslení, pohyb správným
 * směrem, jeho skóre, životy případně chycení duchem
 *
 * @author Petr
 */
public class Player {

    public Direction dir;
    public Direction dirRequest;
    public boolean hold;
    public int score;

    private boolean holdAnim;
    private final Pacman pacman1;
    private final Pacman pacman2;
    private BufferedImage pacman3;
    private int lives;
    private int absoluteX;
    private int absoluteY;
    private int relativeX;
    private int relativeY;
    private final GamePanel game;
    private int anim;
    private int count;

    private class Pacman {

        BufferedImage left;
        BufferedImage right;
        BufferedImage up;
        BufferedImage down;
    };

    /**
     * konstruktor hráče
     *
     * @param game kontext hry
     * @throws IOException
     */
    public Player(GamePanel game) throws IOException {
        this.game = game;
        pacman1 = new Pacman();
        pacman2 = new Pacman();
        loadImages();
    }

    /**
     * vyresetování proměnných vztažených k hráči volá se jen když začína hra
     */
    public void reset() {
        lives = 3;
        score = 0;
        anim = 1;
        count = 0;
        hold = true;
        holdAnim = false;
        dir = Direction.LEFT;
        dirRequest = Direction.LEFT;
        resetPosition();
    }

    /**
     *
     * @return relativní horizontální pozici
     */
    public int getRelativeX() {
        return relativeX;
    }

    /**
     *
     * @return relativní vertikální pozici
     */
    public int getRelativeY() {
        return relativeY;
    }

    /**
     *
     * @return počet životů
     */
    public int getLives() {
        return lives;
    }

    /**
     *
     * @return dosažené scóre
     */
    public int getScore() {
        return score;
    }

    /**
     * zkontoroluje chycení duchem
     * @return konec hry
     */
    public boolean checkColisionWithGhost() {
        if (game.gotCaught(relativeX, relativeY)) {
            if (--lives == 0) {
                game.stopGame();
                return true;
            } else {
                resetPosition();
                game.death();
            }
        }
        return false;
    }

    /**
     * pohne pacmanem určitým směrem a vykreslí ho
     *
     * @param g grafický kontext
     */
    public void draw(Graphics g) {
        for (int i = 0; i < Const.playerSpeed; i++) {
            move();
            if(checkColisionWithGhost()) break;
        }
        switch (anim) {
            case 0:
                g.drawImage(getImageForDirection(pacman1),
                        absoluteX - Const.iconSize / 2,
                        absoluteY - Const.iconSize / 2,
                        Const.iconSize, Const.iconSize, game);
                break;
            case 1:
                g.drawImage(getImageForDirection(pacman2),
                        absoluteX - Const.iconSize / 2,
                        absoluteY - Const.iconSize / 2,
                        Const.iconSize, Const.iconSize, game);
                break;
            case 2:
                g.drawImage(pacman3, absoluteX - Const.iconSize / 2,
                        absoluteY - Const.iconSize / 2,
                        Const.iconSize, Const.iconSize, game);
                break;
        }

    }

    private void resetPosition() {
        relativeX = 14;
        relativeY = 23;
        absoluteX = game.absolutePositionX(relativeX);
        absoluteY = game.absolutePositionY(relativeY);
        if (dir == Direction.UP || dir == Direction.DOWN) {
            dir = Direction.LEFT;
        }
    }

    private void move() {
        if (!hold) {
            if (!holdAnim && (++count % Const.animDelay) == 0) {
                anim = ++anim % 3;
            }
            if ((currentMazeData() & 16) == 16) {
                score += Const.pointValue;
                game.mazeGrid.get(relativeY)[relativeX] -= 16;
            }
            if (isDecisionPoint()) {
                if ((dirRequest == Direction.LEFT && canGoLeft())
                        || (dirRequest == Direction.RIGHT && canGoRight())
                        || (dirRequest == Direction.UP && canGoUp())
                        || (dirRequest == Direction.DOWN && canGoDown())) {
                    dir = dirRequest;
                    holdAnim = false;
                } else if (!(dir == Direction.LEFT && canGoLeft())
                        && !(dir == Direction.RIGHT && canGoRight())
                        && !(dir == Direction.UP && canGoUp())
                        && !(dir == Direction.DOWN && canGoDown())) {
                    holdAnim = true;
                }
            }
            if (!holdAnim) {
                if (dir == Direction.LEFT) {
                    absoluteX += Const.baseSpeed * dir.value();
                    absoluteX = (game.getWidth() + absoluteX) % game.getWidth();
                } else if (dir == Direction.RIGHT) {
                    absoluteX += Const.baseSpeed * dir.value();
                    absoluteX = (game.getWidth() + absoluteX) % game.getWidth();
                } else if (dir == Direction.UP) {
                    absoluteY += Const.baseSpeed * dir.value();
                    absoluteY = (game.getHeight() + absoluteY) % game.getHeight();
                } else if (dir == Direction.DOWN) {
                    absoluteY += Const.baseSpeed * dir.value();
                    absoluteY = (game.getHeight() + absoluteY) % game.getHeight();
                }
            }
            relativeX = (Const.gridWidth + game.relativePositionX(absoluteX)) % Const.gridWidth;
            relativeY = (Const.gridHeight + game.relativePositionY(absoluteY)) % Const.gridHeight;
        }
    }

    private boolean isDecisionPoint() {
        return (absoluteX == game.absolutePositionX(relativeX))
                && (absoluteY == game.absolutePositionY(relativeY));
    }

    private byte currentMazeData() {
        return game.mazeGrid.get(relativeY)[relativeX];
    }

    private void loadImages() throws IOException {
        pacman1.right = ImageIO.read(new File(Const.imagePath + "pacman1.png"));
        pacman1.left = rotate(pacman1.right, 180);
        pacman1.up = rotate(pacman1.right, 270);
        pacman1.down = rotate(pacman1.right, 90);
        pacman2.right = ImageIO.read(new File(Const.imagePath + "pacman2.png"));
        pacman2.left = rotate(pacman2.right, 180);
        pacman2.up = rotate(pacman2.right, 270);
        pacman2.down = rotate(pacman2.right, 90);
        pacman3 = ImageIO.read(new File(Const.imagePath + "pacman3.png"));

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

    private boolean canGoUp() {
        return (currentMazeData() & 2) != 2;
    }

    private boolean canGoDown() {
        return (currentMazeData() & 8) != 8;
    }

    private boolean canGoLeft() {
        return (currentMazeData() & 1) != 1;
    }

    private boolean canGoRight() {
        return (currentMazeData() & 4) != 4;
    }
}
