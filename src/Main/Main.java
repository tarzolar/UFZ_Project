package Main;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import model.Model;
import view.Grid;

/**
 * The main class containing the entry point of the program.
 * This model was implemented by Tomas Arzola Roeber.
 */
public class Main {

    /**
     * The main method that starts the application.
     * @param args the command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Fisher-Model");
        
        frame.setPreferredSize(new Dimension(1300, 600));
        
        int scale = 100;
        int upscale = 2500;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scales.txt"))) {
            writer.write(scale + " " + upscale);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Model model = new Model(scale, upscale, 10, false);
        
        Grid grid = new Grid(model);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setContentPane(grid);
        
        frame.setVisible(true);
        
        frame.pack();
    }
}
