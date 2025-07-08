import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static JButton changeColor;
    public static JPaintArea paint;
    public static PaintTools tools;
    //temp
    public static JButton save = new JButton("Save");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JPaintArea Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 900);
            frame.setLocationRelativeTo(null);

            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.setBackground(new Color(100, 100, 100));

            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setOpaque(false);

            paint = new JPaintArea(32, 32, 500, 500);
            paint.setBackground(new Color(150, 150, 150));

            centerPanel.add(paint);
            contentPane.add(centerPanel, BorderLayout.CENTER);

            tools = new PaintTools(480, 30, paint);
            contentPane.add(tools, BorderLayout.SOUTH);

            // contentPane.add(new JPanel(), BorderLayout.WEST);
            // contentPane.add(new JPanel(), BorderLayout.EAST);

            save.addActionListener(new events2(paint));
            contentPane.add(save, BorderLayout.WEST);

            frame.setVisible(true);
        });
    }
}

class events2 implements ActionListener {

    public JPaintArea area;
    public events2(JPaintArea area){
        this.area = area;
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        area.importImage("C:/Users/Mastermind/Downloads/Exported.png");

    }
}

