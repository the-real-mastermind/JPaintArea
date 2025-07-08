import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PaintTools extends JPanel{

    public PaintArea area;
    public BrushSizeField brushSize;
    public BrushColorRGB brushColor;

    public events l = new events();

     public PaintTools(int width, int height, PaintArea area){
         this.area = area;
         brushSize = new BrushSizeField(width / 4, 20, area);
         this.add(brushSize);

         this.add(Box.createRigidArea(new Dimension(width / 5, 0)));

         brushColor = new BrushColorRGB(width / 4, 20, area);
         this.add(brushColor);

         this.setPreferredSize(new Dimension(width,height));

         this.setLayout(new FlowLayout());
     }
}



class BrushSizeField extends JPanel {

    public int width, height;
    public PaintArea area;

    public JTextField textField = new JTextField();
    public JLabel label = new JLabel();

    public BrushSizeField(int width, int height, PaintArea area) {
        this.width = width;
        this.height = height;
        this.area = area;

        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        label.setText("Brush Size ");
        label.setPreferredSize(new Dimension((width / 3) * 2, height));
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        textField.setPreferredSize(new Dimension(width / 3, height));
        textField.setText("1");

        this.add(label);
        this.add(textField);

        // 👇 Add listener to update brush size on text change
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateBrushSize();
            }

            public void removeUpdate(DocumentEvent e) {
                updateBrushSize();
            }

            public void changedUpdate(DocumentEvent e) {
                updateBrushSize();
            }

            private void updateBrushSize() {
                try {
                    String size = textField.getText().trim();

                    if(size.isEmpty()){
                        size = "1";
                        SwingUtilities.invokeLater(() -> {
                            textField.setText("1");
                        });
                    }
                    int newSize = Integer.parseInt(size);

                    if (newSize > 0) {
                        area.setBrushSize(newSize);
                        area.repaint();
                    }
                } catch (NumberFormatException ex) {
                    SwingUtilities.invokeLater(() -> {
                        System.out.println(ex.getMessage());
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Brush size must be a number");
                        textField.setText("1");
                    });


                }
            }
        });
    }
}


class BrushColorRGB extends JPanel {

    public int width, height;
    public Color color;
    public PaintArea area;

    public int r, g, b;

    public JTextField textR = new JTextField();
    public JTextField textG = new JTextField();
    public JTextField textB = new JTextField();

    public JPanel colorDisplay = new JPanel();

    private boolean isUpdating = false;


    public BrushColorRGB(int width, int height, PaintArea area) {
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


class events implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}