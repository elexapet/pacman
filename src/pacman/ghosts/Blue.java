
package pacman.ghosts;

import java.io.IOException;
import pacman.Const;
import pacman.GamePanel;

/**
 *
 * @author Petr
 */
public class Blue extends AbstractGhost{

    private AbstractGhost red;
    
    public Blue(GamePanel game, AbstractGhost red) throws IOException {
        super(game, "blue.png");
        this.red = red;
        startX = 14;
        startY = 11;
    }

    @Override
    void selectTarget() {
        int x,y;
        if (game.getPlayer().dir == Const.Direction.LEFT
                || game.getPlayer().dir == Const.Direction.RIGHT) {
            x = game.getPlayer().getRelativeX() + 2 * game.getPlayer().dir.value();
            y = game.getPlayer().getRelativeY();
        } else {
            x = game.getPlayer().getRelativeX();
            y = game.getPlayer().getRelativeY() + 2 * game.getPlayer().dir.value();
        }
        targetX = 2*(x - red.getRelativeX()) + red.getRelativeX();
        targetY = 2*(y - red.getRelativeY()) + red.getRelativeY();
    }

    @Override
    void selectHome() {
        targetX = 27;
        targetY = 30;
    }
    
}
