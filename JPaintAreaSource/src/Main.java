import javax.swing.*;
import java.awt.*;

public class Main{

    //Frame and JPaintArea
    public static JPaintArea paint = new JPaintArea(32, 32, 500, 500);
    public static JFrame frame = new JFrame("JPaintAreaShowcase");

    public static void main(String[] args) {

        // Frame init
        frame.setSize(new Dimension(900, 900));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.gray);
        frame.setLayout(null);

        // Paint init
        paint.setBounds(0, 0, 900, 900);
        paint.setUseGrid(true);
        paint.setGridSize(2);

        // Adding
        frame.add(paint);

        // Enable
        frame.setVisible(true);
    }
}
