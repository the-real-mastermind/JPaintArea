import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FileImportExport extends JPanel {

    public JButton ImportFile = new JButton("Import");
    public JButton SaveAs = new JButton("Save As");
    public JButton SaveButton = new JButton("Save");

    public ArrayList<JButton> buttons = new ArrayList<JButton>();

    public FileImportExport(int width, int height, JPaintArea area){
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        buttons.add(ImportFile);
        buttons.add(SaveAs);
        buttons.add(SaveButton);

        for(JButton button : buttons){
            button.setPreferredSize(new Dimension(width / 3, height));
            button.setFocusPainted(false);
            button.setBackground(Color.white);
        }

        ImportFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser filePicker = new JFileChooser();

                filePicker.showDialog(JOptionPane.getRootFrame(), "Load");

                String path = filePicker.getSelectedFile().getPath();
                System.out.println("Loading: " + path);

                try{
                    area.importImage(path);
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Please select a readable image file");
                }
                System.out.println("Finished loading " + path);
            }
        });


        SaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser filePicker = new JFileChooser();

                filePicker.showDialog(JOptionPane.getRootFrame(), "Save as");

                String path = filePicker.getSelectedFile().getPath();
                System.out.println("Saving: " + path);

                try{
                    System.out.println(path.split(".").length);
                    if(path.split(".").length == 0){
                        path += ".png";
                    }
                    area.exportImage(path);
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Please select a readable image file");
                }
                System.out.println("Finished saving " + path);
            }
        });

        this.add(Box.createRigidArea(new Dimension(width / 12, 0)));
        this.add(ImportFile);
        this.add(SaveAs);

        this.add(SaveButton);

    }

}