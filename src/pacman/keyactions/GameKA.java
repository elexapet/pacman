/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pacman.keyactions;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import pacman.Const.Direction;
import pacman.GamePanel;

/**
 *
 * @author Petr
 */
public class GameKA extends KeyAdapter{
    
    final private GamePanel gp; 
    private boolean paused; 

    public GameKA(GamePanel gp) {
        this.gp = gp;
        paused = false;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (!gp.isInGame() && key == KeyEvent.VK_ENTER){ 
            gp.startGame();
        }
        else switch(key){
            case KeyEvent.VK_UP:
                gp.getPlayer().dir=Direction.UP;
                break;
            case KeyEvent.VK_DOWN:
                gp.getPlayer().dir=Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
                gp.getPlayer().dir=Direction.LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                gp.getPlayer().dir=Direction.RIGHT;
                break;
            case KeyEvent.VK_SPACE:
                if (paused) {
                    gp.timer.start();
                    paused = false;
                } else {
                    gp.timer.stop();
                    paused = true;
                }
        }
    }
    
}
