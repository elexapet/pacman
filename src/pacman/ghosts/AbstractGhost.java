

package pacman.ghosts;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import pacman.Const;
import pacman.Const.Direction;
import pacman.GamePanel;

/**
 * Rodičovská třída pro duchy
 * @author Petr
 */
public abstract class AbstractGhost {

    private final BufferedImage ghostImage;
    protected final GamePanel game;
    protected Const.Direction dir;
    protected Const.Direction nextDir;
    private int absoluteX;
    private int absoluteY;
    protected int relativeX;
    protected int relativeY;
    protected int startX;
    protected int startY;
    protected int targetX;
    protected int targetY;
    
    /**
     * Konstruktor abstraktního ducha
     * @param game herní kontext
     * @param imageName název souboru s ikonou ducha
     * @throws IOException
     */
    public AbstractGhost(GamePanel game, String imageName) throws IOException {
        this.game = game;
        ghostImage = ImageIO.read(new File(Const.imagePath + imageName));
    }

    /**
     * Vykreslení ducha, posunutí a případně výpočet
     * jeho rozhodnutí na další křižovatce.
     * @param g grafický kontext
     */
    public void draw(Graphics g) {
        for (int i = 0; i < Const.ghostSpeed; i++) {
            g.drawImage(ghostImage, absoluteX - Const.iconSize / 2,
                absoluteY - Const.iconSize / 2, Const.iconSize,
                Const.iconSize, game);
            
            move();
            game.getPlayer().checkColision();
        }
    }
    
    /**
     * nastavení ducha do výchozí pozice
     */
    public void reset(){
        nextDir = Const.Direction.UP;
        relativeX = startX;
        relativeY = startY;
        absoluteX = game.absolutePositionX(relativeX);
        absoluteY = game.absolutePositionY(relativeY);
        dir = Const.Direction.LEFT;
    }
    
    /**
     * převrácení směru duchů
     * voláno při změně módu
     */
    public void flipDirecition() {
        if (dir == Direction.LEFT) {
            dir = Direction.RIGHT;
        } else if (dir == Direction.RIGHT) {
            dir = Direction.LEFT;
        } else if (dir == Direction.UP) {
            dir = Direction.DOWN;
        } else {
            dir = Direction.UP;
        }
    }
    
    /**
     *
     * @return relativní vertikální pozici ducha
     */
    public int getRelativeX() {
        return relativeX;
    }

    /**
     *
     * @return relativní vertikální pozici ducha
     */
    public int getRelativeY() {
        return relativeY;
    }
    
    /**
     * nastavení odpovídajícího cíle
     */
    abstract void selectTarget();
    
    /**
     * nastavení domovského cíle
     */
    abstract void selectHome();

    private void alterPosition() {
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            absoluteX += Const.baseSpeed * dir.value();
            absoluteX = (game.getWidth() + absoluteX) % game.getWidth();
            relativeX = (Const.gridWidth + game.relativePositionX(absoluteX))
                    % Const.gridWidth;

        } else {
            absoluteY += Const.baseSpeed * dir.value();
            absoluteY = (game.getHeight() + absoluteY) % game.getHeight();
            relativeY = (Const.gridHeight + game.relativePositionY(absoluteY))
                    % Const.gridHeight;
        }
    }
    
    private void move() {
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            if (isDecisionPoint()) {
                int futureX = (Const.gridWidth + relativeX + dir.value()) % Const.gridWidth;
                if (isPlanningPoint(futureX, relativeY)) {
                    planNextTurn();
                }
                if (isPlanningPoint(relativeX, relativeY)) {
                    dir = nextDir;
                } else if (isOneWayTurn()) {
                    if (canGoUp()) {
                        dir = Direction.UP;
                    } else {
                        dir = Direction.DOWN;
                    }
                } 
            } 
        }
        else if (dir == Direction.UP || dir == Direction.DOWN) {
            if (isDecisionPoint()) {
                if (isPlanningPoint(relativeX, relativeY + dir.value())) {
                    planNextTurn();
                }
                if (isPlanningPoint(relativeX, relativeY)) {                
                    dir = nextDir;
                } else if (isOneWayTurn()) {
                    if (canGoLeft()) {
                        dir = Direction.LEFT;
                    } else {
                        dir = Direction.RIGHT;
                    }
                } 
            } 
        }
        alterPosition();
    }

    private boolean isDecisionPoint() {
        return (absoluteX == game.absolutePositionX(relativeX)) 
                && (absoluteY == game.absolutePositionY(relativeY));
    }

    private boolean isPlanningPoint(int x, int y) {
        int data = game.mazeGrid.get(y)[x] & 15;
        return (data == 0) || (data == 1) || (data == 2) || (data == 4) || (data == 8);
    }

    private boolean isOneWayTurn() {
        return ((game.mazeGrid.get(relativeY)[relativeX] & 15) % 3) == 0;
    }

    protected boolean canGoUp() {
        return (game.mazeGrid.get(relativeY)[relativeX] & 2) != 2;
    }

    protected boolean canGoDown() {
        return (game.mazeGrid.get(relativeY)[relativeX] & 8) != 8;
    }

    protected boolean canGoLeft() {
        return (game.mazeGrid.get(relativeY)[relativeX] & 1) != 1;
    }

    protected boolean canGoRight() {
        return (game.mazeGrid.get(relativeY)[relativeX] & 4) != 4;
    }

    private void planNextTurn() {
        double up = Double.MAX_VALUE;
        double down = Double.MAX_VALUE;
        double left = Double.MAX_VALUE;
        double right = Double.MAX_VALUE;
        double min;
        
        if (game.isScatter()) {
            selectHome();
        } else {
            selectTarget();
        }
        //Přicházím zleva nebo zprava
        if (dir == Const.Direction.LEFT || dir == Const.Direction.RIGHT) {
            relativeX += dir.value();
            if (canGoUp()) {
                up = Math.hypot(targetX - relativeX, targetY - (relativeY - 1));
            }
            if (canGoDown()) {
                down = Math.hypot(targetX - relativeX, targetY - (relativeY + 1));
            }
            if (canGoLeft() && dir == Const.Direction.LEFT) {
                left = Math.hypot(targetX - (relativeX - 1), targetY - relativeY);
            }
            if (canGoRight() && dir == Const.Direction.RIGHT) {
                right = Math.hypot(targetX - (relativeX + 1), targetY - relativeY);
            }
            relativeX -= dir.value();
            //spočti nejraktší přímou cestu z odpovídajícího bodu k cíly
            if (dir == Const.Direction.LEFT) {
                min = Math.min(left, Math.min(up, down));
            } else {
                min = Math.min(right, Math.min(up, down));
            }
            //přicházím ze zdola nebo ze zhora
        } else {
            relativeY += dir.value();
            if (canGoUp() && dir == Const.Direction.UP) {
                up = Math.hypot(targetX - relativeX, targetY - (relativeY - 1));
            }
            if (canGoDown() && dir == Const.Direction.DOWN) {
                down = Math.hypot(targetX - relativeX, targetY - (relativeY + 1));
            }
            if (canGoLeft()) {
                left = Math.hypot(targetX - (relativeX - 1), targetY - relativeY);
            }
            if (canGoRight()) {
                right = Math.hypot(targetX - (relativeX + 1), targetY - relativeY);
            }
            relativeY -= dir.value();
            //spočti nejraktší přímou cestu z odpovídajícího bodu k cíly
            if (dir == Const.Direction.UP) {
                min = Math.min(up, Math.min(left, right));
            } else {
                min = Math.min(down, Math.min(left, right));
            }
        }
        //urči budoucí směr na základě nejraktší cesty
        if (min == up) {
            nextDir = Const.Direction.UP;
        } else if (min == down) {
            nextDir = Const.Direction.DOWN;
        } else if (min == left) {
            nextDir = Const.Direction.LEFT;
        } else {
            nextDir = Const.Direction.RIGHT;
        }
    }

    
}
