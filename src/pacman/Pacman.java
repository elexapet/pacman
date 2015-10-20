/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pacman;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * spustitelná metoda pro vytvoření rámu a přidání hlavní herní plochy
 * @author Petr
 */
public class Pacman {

          
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            final JFrame f = new JFrame();

            f.setTitle("Pac-Man");
            f.add(new GamePanel());
            f.setResizable(false);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    f.setVisible(true);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(Pacman.class.getName()).log(Level.SEVERE, "Images not loaded!", ex);
        }
    }
    
}
