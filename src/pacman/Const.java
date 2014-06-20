/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pacman;

/**
 *
 * @author Petr
 */
public interface Const {
    public final static String introMessage = "Press 'Enter' to start!";
    public final static int liveSize = 25;
    public final static int padding = 5;
    public final static int pointValue = 10;
    public final static String imagePath = "images/";
    public final static int animDelay = 2;
    public final static int speed = 3;
    public enum Direction{
        LEFT(-1), RIGHT(1), UP(-1), DOWN(1), STOP(0);
        
        private final int value;
        
        private Direction(int value){
            this.value = value;
        }
        
        public int value(){
            return value;
        }
    }
    
}
