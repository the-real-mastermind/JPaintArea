import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class Main{

    public static JButton changeColor;
    public static PaintArea paint;

    public static PaintTools tools;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fast Grid");
            paint = new PaintArea(32, 32, 500, 500);
            frame.setLayout(new FlowLayout());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 900);
            frame.getContentPane().setBackground(new Color(100,100,100));
            frame.add(paint);



            tools = new PaintTools(480, 30, paint);

            frame.add(tools);

            frame.setVisible(true);




        });
    }
}

