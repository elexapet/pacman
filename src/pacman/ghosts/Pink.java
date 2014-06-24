
package pacman.ghosts;

import java.io.IOException;
import pacman.Const;
import pacman.GamePanel;

/**
 *
 * @author Petr
 */
public class Pink extends AbstractGhost {

    public Pink(GamePanel game) throws IOException {
        super(game, "pink.png");
        startX = 13;
        startY = 11;
    }

    @Override
    void selectTarget() {
        if (game.getPlayer().dir == Const.Direction.LEFT
                || game.getPlayer().dir == Const.Direction.RIGHT) {
            targetX = game.getPlayer().getRelativeX() + 4 * game.getPlayer().dir.value();
            targetY = game.getPlayer().getRelativeY();
        } else {
            targetX = game.getPlayer().getRelativeX();
            targetY = game.getPlayer().getRelativeY() + 4 * game.getPlayer().dir.value();
        }
    }

    @Override
    void selectHome() {
        targetX = 2;
        targetY = 0;
    }
    
}
