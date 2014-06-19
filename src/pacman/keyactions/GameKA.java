/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pacman.keyactions;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import pacman.GamePanel;

/**
 *
 * @author Petr
 */
public class GameKA extends KeyAdapter{
    
    final private GamePanel gp; 

    public GameKA(GamePanel gp) {
        this.gp = gp;
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
                
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
        }
    }
    
}
