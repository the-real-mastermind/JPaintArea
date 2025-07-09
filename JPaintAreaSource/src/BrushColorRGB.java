import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BrushColorRGB extends JPanel {

    public int width, height;
    public Color color;
    public JPaintArea area;

    public int r, g, b;

    public JTextField textR = new JTextField();
    public JTextField textG = new JTextField();
    public JTextField textB = new JTextField();

    public JPanel colorDisplay = new JPanel();

    private boolean isUpdating = false;


    public BrushColorRGB(int width, int height, JPaintArea area) {
        this.width = width;
        this.height = height;
        this.area = area;

        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        colorDisplay.setPreferredSize(new Dimension(height,height));

        textR.setPreferredSize(new Dimension(width / 20, height));
        textR.setText("0");
        textG.setPreferredSize(new Dimension(width / 20, height));
        textG.setText("0");
        textB.setPreferredSize(new Dimension(width / 20, height));
        textB.setText("0");

        colorDisplay.setBackground(new Color(r,g,b));
        colorDisplay.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color chosenColor = JColorChooser.showDialog(
                        BrushColorRGB.this, "Choose Brush Color", new Color(r, g, b));

                if (chosenColor != null) {
                    r = chosenColor.getRed();
                    g = chosenColor.getGreen();
                    b = chosenColor.getBlue();

                    isUpdating = true;
                    textR.setText(String.valueOf(r));
                    textG.setText(String.valueOf(g));
                    textB.setText(String.valueOf(b));
                    isUpdating = false;

                    colorDisplay.setBackground(chosenColor);
                    area.setActiveColor(chosenColor);
                }
            }



            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(colorDisplay);
        this.add(Box.createRigidArea(new Dimension(width / 40, 0)));

        this.add(textR);
        this.add(textG);
        this.add(textB);

        DocumentListener l = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateBrushColor();
            }

            public void removeUpdate(DocumentEvent e) {
                updateBrushColor();
            }

            public void changedUpdate(DocumentEvent e) {
                updateBrushColor();
            }

            private void updateBrushColor() {
                if (isUpdating){
                    return;
                };

                try {
                    String rText = textR.getText().trim();
                    String gText = textG.getText().trim();
                    String bText = textB.getText().trim();

                    if (rText.isEmpty()) {
                        rText = "0";
                    }
                    if (gText.isEmpty()) {
                        gText = "0";
                    }
                    if (bText.isEmpty()) {
                        bText = "0";
                    }

                    r = Integer.parseInt(rText);
                    g = Integer.parseInt(gText);
                    b = Integer.parseInt(bText);

                    if (r > 255 ) {
                        r = 255;
                    }
                    if (g > 255 ) {
                        g = 255;
                    }
                    if (b > 255 ) {
                        b = 255;
                    }

                    SwingUtilities.invokeLater(() -> {
                        isUpdating = true;
                        textR.setText(String.valueOf(r));
                        textG.setText(String.valueOf(g));
                        textB.setText(String.valueOf(b));
                        isUpdating = false;
                    });

                } catch (NumberFormatException ex) {
                    SwingUtilities.invokeLater(() -> {
                        isUpdating = true;
                        System.out.println(ex.getMessage());
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Color values must be a valid number (0–255)");
                        textR.setText("0");
                        textG.setText("0");
                        textB.setText("0");
                        isUpdating = false;
                    });

                    r = g = b = 255;
                }

                Color newColor = new Color(r,g,b);
                colorDisplay.setBackground(newColor);
                area.setActiveColor(newColor);

            }


        };

        textR.getDocument().addDocumentListener(l);
        textG.getDocument().addDocumentListener(l);
        textB.getDocument().addDocumentListener(l);
    }


}
