import javax.swing.*;
import java.awt.*;

public class Main {

    public static JButton changeColor;
    public static JPaintArea paint;
    public static PaintTools tools;
    public static FileImportExport file;
    public static JScrollPane scrollPane; // Scroll pane reference

    // temp
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

            // Wrapper panel to center the paint area
            JPanel paintWrapper = new JPanel(new GridBagLayout());
            paintWrapper.setBackground(new Color(100, 100, 100));
            paintWrapper.add(paint);

            // Scroll pane holding the wrapper panel
            scrollPane = new JScrollPane(paintWrapper);
            scrollPane.setPreferredSize(new Dimension(700, 700));
            scrollPane.setBorder(null);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getViewport().setBackground(new Color(100, 100, 100));

            centerPanel.add(scrollPane);
            contentPane.add(centerPanel, BorderLayout.CENTER);

            tools = new PaintTools(480, 30, paint);
            contentPane.add(tools, BorderLayout.SOUTH);

            file = new FileImportExport(100, 40, paint);
            contentPane.add(file, BorderLayout.NORTH);

            frame.setVisible(true);
        });
    }
}
