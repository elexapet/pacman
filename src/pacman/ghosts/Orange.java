
package pacman.ghosts;

import java.io.IOException;
import pacman.GamePanel;

/**
 *
 * @author Petr
 */
public class Orange extends AbstractGhost{
    
    public Orange(GamePanel parent) throws IOException {
        super(parent, "orange.png");
        startX = 13;
        startY = 11;
    }

    @Override
    public void selectTarget() {
        targetX = game.getPlayer().getRelativeX();
        targetY = game.getPlayer().getRelativeY();
        if (Math.hypot(targetX - relativeX, targetY - relativeY) < 8) {
            selectHome();
        }
    }
    
    @Override
    void selectHome() {
        targetX = 0;
        targetY = 30;
    }
}
