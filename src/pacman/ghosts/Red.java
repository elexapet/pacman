/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pacman.ghosts;

import java.io.IOException;
import pacman.GamePanel;

/**
 *
 * @author Petr
 */
public class Red extends AbstractGhost{

    public Red(GamePanel parent) throws IOException {
        super(parent);
    }

    @Override
    void planNextTurn() {
        System.out.println("plannig");
    }
    
}
