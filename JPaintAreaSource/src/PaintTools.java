import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PaintTools extends JPanel{

    public JPaintArea area;
    public BrushSizeField brushSize;
    public BrushColorRGB brushColor;

     public PaintTools(int width, int height, JPaintArea area){
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
