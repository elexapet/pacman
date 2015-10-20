
package pacman.ghosts;

import java.io.IOException;
import pacman.GamePanel;

/**
 * 
 * @author Petr
 */
public class Red extends AbstractGhost {

    public Red(GamePanel parent) throws IOException {
        super(parent, "red.png");
        startX = 14;
        startY = 11;
    }

    @Override
    void selectTarget() {
       targetX = game.getPlayer().getRelativeX();
       targetY = game.getPlayer().getRelativeY();
    }
    
    @Override
    void selectHome() {
        targetX = 25;
        targetY = 0;
    }

    
    
}
