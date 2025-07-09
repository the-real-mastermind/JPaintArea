import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class BrushSizeField extends JPanel {

    public int width, height;
    public JPaintArea area;

    public JTextField textField = new JTextField();
    public JLabel label = new JLabel();

    private boolean isUpdating = false;


    public BrushSizeField(int width, int height, JPaintArea area) {
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

                if (isUpdating){
                    return;
                };

                try {
                    String size = textField.getText().trim();
                    int newSize;

                    if(!size.isEmpty()){
                        newSize = Integer.parseInt(size);
                    }else{
                        isUpdating = true;
                        newSize = 1;
                        SwingUtilities.invokeLater(() -> {

                        });
                        isUpdating = false;
                    }

                    if (newSize > 0) {
                        area.setBrushSize(newSize);
                        area.repaint();
                    }

                } catch (NumberFormatException ex) {
                    SwingUtilities.invokeLater(() -> {
                        isUpdating = true;
                        System.out.println(ex.getMessage());
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Brush size must be a number");
                        textField.setText("1");
                        area.setBrushSize(1);
                        area.repaint();
                        isUpdating = false;
                    });


                }
            }
        });
    }
}