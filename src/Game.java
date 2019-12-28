
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game implements Runnable {
    

    public void run() {
        
        final JFrame frame = new JFrame("Solitaire");
        frame.setLocation(300, 300);
        GameBoard gb = new GameBoard();
        frame.add(gb);
//        Timer timer = new Timer(100, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                frame.pack();
//            }
//        });
//        timer.start();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}