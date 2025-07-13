import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A JComponent that allows the user to paint pixels onto a canvas
 */
public class JPaintArea extends JPanel {

    /**
     * The cell size of the canvas
     * */
    private int cellX, cellY;
    protected int gridWidth, gridHeight;

    /**
     * The size in pixels of the JPaintArea
     * */
    int panelWidth, panelHeight;

    /**
     * The amount of cells the users brush paints
     * */
    private int brushSize = 1;

    /**
     * The size of each cell in pixels
     * */
    protected int cellWidth, cellHeight;

    /**
     * The amount of cells between each grid line
     * */
    private int gridSize = 16;

    /**
     * Is the grid enabled
     * */
    private boolean useGrid;

    /**
     * a map of all the painted cells. Contains the position and the color of the cells
     * */
    protected LinkedHashMap<Point, Color> paintedCells = new LinkedHashMap<>();

    /**
     * The hovered cell
     * */
    protected Point hoveredCell = null;

    /**
     * The active painting color
     * */
    private Color activeColor = Color.black;

    /**
     * The color of the grid
     * */
    private Color gridColor = new Color(0xFFFFFFF);

    /**
     * The event listener
     * */
    public listener listener = new listener();

    /**
     * is the canvas enabled for painting
     * */
    private boolean isEnabled = true;

    /***.
     * Creates a Paint area with a set cell size and size for the paint area. Does not have a grid by default. Square size is recommended
     * @param cellSize the size in cells of the canvas
     * @param panelWidth the x size of the JPaintArea
     * @param panelHeight the y size of the JPaintArea
     */
    public JPaintArea(int cellSize, int panelWidth, int panelHeight) {
        create(cellSize, cellSize, panelWidth, panelHeight);
    }

    /***.
     * Creates a Paint area with a set x and y cell size and size for the paint area. Does not have a grid by default. Square size is recommended
     * @param cellX the x size in cells of the canvas
     * @param cellY the y size in cells of the canvas
     * @param panelWidth the x size of the JPaintArea
     * @param panelHeight the y size of the JPaintArea
     */
    public JPaintArea(int cellX, int cellY, int panelWidth, int panelHeight) {
        create(cellX, cellY, panelWidth, panelHeight);
    }

    public void create(int cellX, int cellY, int panelWidth, int panelHeight){
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.cellX = cellX;
        this.cellY = cellY;
        this.gridWidth = cellX;
        this.gridHeight = cellY;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setSize(panelWidth, panelHeight);
        resizePaintArea(gridWidth, gridHeight);
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);
        listener.paint = this;
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    /**
     * Resizes the paint area to fit a new cell Size
     * @param newCellX the new x cell size
     * @param newCellY the new y cell size
     */
    public void resizePaintArea(int newCellX, int newCellY) {
        this.cellX = newCellX;
        this.cellY = newCellY;
        this.gridWidth = newCellX;
        this.gridHeight = newCellY;
        int cellSizeW = panelWidth / gridWidth;
        int cellSizeH = panelHeight / gridHeight;
        int cellSize = Math.max(1, Math.min(cellSizeW, cellSizeH));
        this.cellWidth = cellSize;
        this.cellHeight = cellSize;
        repaint();
    }

    /**
     * Exports the JPaintArea painted image as a file to the users local system
     * @param path The path to the file to be exported to
     */
    public void exportImage(String path){
        BufferedImage bufferedImage = new BufferedImage(gridWidth, gridHeight, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                Point p = new Point(x, y);
                Color color = paintedCells.getOrDefault(p, new Color(0, 0, 0, 0));
                bufferedImage.setRGB(x, y, color.getRGB());
            }
        }
        try {
            File outputfile = new File(path);
            String[] type = path.split("\\.");
            String fileType = type[type.length - 1];
            ImageIO.write(bufferedImage, fileType, outputfile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Unable to export image");
            throw new RuntimeException("Failed to export image", e);
        }
    }

    /**
     * Imports an image and adjusts the cell count to match the image resolution,
     * while preserving aspect ratio and fitting it inside the existing panel size.
     * @param path Path to the image file
     */
    public void importImage(String path) {
        File imageFile = new File(path);
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) throw new IOException("Unsupported or unreadable image: " + path);
            BufferedImage converted = new BufferedImage(
                    image.getWidth(), image.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = converted.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            image = converted;
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();
            double aspectRatio = (double) imgWidth / imgHeight;
            int scaledWidth = panelWidth;
            int scaledHeight = (int) (panelWidth / aspectRatio);
            if (scaledHeight > panelHeight) {
                scaledHeight = panelHeight;
                scaledWidth = (int) (panelHeight * aspectRatio);
            }
            BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaledImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
            g2.dispose();
            this.gridWidth = scaledWidth;
            this.gridHeight = scaledHeight;
            this.cellX = gridWidth;
            this.cellY = gridHeight;
            this.cellWidth = 1;
            this.cellHeight = 1;
            setPreferredSize(new Dimension(panelWidth, panelHeight));
            setSize(panelWidth, panelHeight);
            paintedCells.clear();
            for (int y = 0; y < scaledHeight; y++) {
                for (int x = 0; x < scaledWidth; x++) {
                    int rgb = scaledImage.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    if (color.getAlpha() > 0) {
                        paintedCells.put(new Point(x, y), color);
                    }
                }
            }
            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
            revalidate();
            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Allows the user to step back a step in their painting
     * */
    public void undo(){
        removeLastEntry(paintedCells);
        revalidate();
        repaint();
    }

    /**
     * Removes the last entry from a LinkedHashMap
     * @param map The LinkedHashMap to remove from
     * */
    public static <K, V> void removeLastEntry(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> last = null;
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        if (last != null) {
            map.remove(last.getKey());
        }
    }

    /**
     * Paints the cells and grid onto the paint area
     * */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                Point p = new Point(x, y);
                Color color = paintedCells.get(p);
                if (color != null) {
                    g.setColor(color);
                    g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                } else {
                    g.setColor(getBackground());
                    g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                }
            }
        }
        if (useGrid) {
            g.setColor(gridColor);
            for (int x = 0; x <= gridWidth; x++) {
                int xPos = x * cellWidth;
                g.drawLine(xPos, 0, xPos, gridHeight * cellHeight);
            }
            for (int y = 0; y <= gridHeight; y++) {
                int yPos = y * cellHeight;
                g.drawLine(0, yPos, gridWidth * cellWidth, yPos);
            }
        }
        if (hoveredCell != null) {
            int hx = hoveredCell.x;
            int hy = hoveredCell.y;
            int rectX, rectY;
            int rectW = cellWidth * brushSize;
            int rectH = cellHeight * brushSize;
            if (brushSize % 2 == 0) {
                rectX = hx * cellWidth;
                rectY = hy * cellHeight;
            } else {
                int half = brushSize / 2;
                rectX = (hx - half) * cellWidth;
                rectY = (hy - half) * cellHeight;
            }
            g.setColor(new Color(activeColor.getRed(), activeColor.getGreen(), activeColor.getBlue(),128));
            g.fillRect(rectX, rectY, rectW, rectH);
            g.setColor(Color.WHITE);
            g.drawRect(rectX, rectY, rectW - 1, rectH - 1);
        }
    }

    //Getters and Setters
    /**
     * Gets the preferred size of the canvas
     * */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(panelWidth, panelHeight);
    }

    /**
     * Returns the current color used to draw the grid
     * @return the grid color
     */
    public Color getGridColor() {
        return gridColor;
    }

    /**
     * Sets the color used to draw the grid
     * @param gridColor the new grid color
     */
    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    /**
     * Returns the size of the grid
     * @return the grid size
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Sets the size of the grid
     * @param gridSize the new grid size
     */
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    /**
     * Returns the current brush size used for painting
     * @return the brush size
     */
    public int getBrushSize() {
        return brushSize;
    }

    /**
     * Sets the brush size used for painting
     * @param brushSize the new brush size
     */
    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    /**
     * Returns the size of a single grid cell as a {@link Dimension} object
     * @return the cell size
     */
    public Dimension getSize() {
        return new Dimension(cellX, cellY);
    }

    /**
     * Returns whether the grid is currently visible
     * @return {@code true} if the grid is shown; {@code false} otherwise
     */
    public boolean isUseGrid() {
        return useGrid;
    }

    /**
     * Sets whether the grid should be visible
     * @param useGrid {@code true} to show the grid; {@code false} to hide it
     */
    public void setUseGrid(boolean useGrid) {
        this.useGrid = useGrid;
    }

    /**
     * Returns the currently active color used for painting
     * @return the active paint color
     */
    public Color getActiveColor() {
        return activeColor;
    }
    /**
     * Sets the currently active color used for painting
     * @param activeColor the new active paint color
     */
    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
    }

    public boolean isPaintEnabled() {
        return isEnabled;
    }

    public void enablePaint() {
        isEnabled = true;
    }

    public void disablePaint() {
        isEnabled = false;
    }
}

//Events
/**
 * The mouse listener for the JPaintArea
 * */
class listener extends MouseAdapter {
    public JPaintArea paint;
    public boolean held;
    private Point lastPaintedCell = null;
    @Override
    public void mousePressed(MouseEvent e) {
       if(!SwingUtilities.isLeftMouseButton(e))
           return;
        held = true;
        paintCell(e);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        held = false;
        if(!SwingUtilities.isLeftMouseButton(e))
            return;
        lastPaintedCell = null;
    }
    @Override
    public void mouseExited(MouseEvent e) {
        if(!SwingUtilities.isLeftMouseButton(e))
            return;
        paint.hoveredCell = null;
        paint.repaint();
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        updateHoveredCell(e);
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        updateHoveredCell(e);
        if (held) {
            paintCell(e);
        }
    }

    /**
     * Updates the cell the user is hovering on so the hover display can be displayed
     * */
    private void updateHoveredCell(MouseEvent e) {
        if (paint.cellWidth <= 0 || paint.cellHeight <= 0) return;
        int mouseX = Math.min(e.getX(), paint.cellWidth * paint.gridWidth - 1);
        int mouseY = Math.min(e.getY(), paint.cellHeight * paint.gridHeight - 1);
        int x = mouseX / paint.cellWidth;
        int y = mouseY / paint.cellHeight;
        Point hovered = new Point(x, y);
        if (!hovered.equals(paint.hoveredCell)) {
            paint.hoveredCell = hovered;
            paint.repaint();
        }
    }

    /**
     * Adds a cell to the painted cells based on the brush size and color
     * */
    void paintCell(MouseEvent e) {
        if(!paint.isPaintEnabled())
            return;
        int mouseX = Math.min(e.getX(), paint.cellWidth * paint.gridWidth - 1);
        int mouseY = Math.min(e.getY(), paint.cellHeight * paint.gridHeight - 1);
        int centerX = mouseX / paint.cellWidth;
        int centerY = mouseY / paint.cellHeight;
        int gridWidth = paint.gridWidth;
        int gridHeight = paint.gridHeight;
        Point centerPoint = new Point(centerX, centerY);
        if (centerPoint.equals(lastPaintedCell)) return;
        lastPaintedCell = centerPoint;
        if (paint.getBrushSize() % 2 == 1) {
            int halfBrush = paint.getBrushSize() / 2;
            for (int dy = -halfBrush; dy <= halfBrush; dy++) {
                for (int dx = -halfBrush; dx <= halfBrush; dx++) {
                    int nx = centerX + dx;
                    int ny = centerY + dy;
                    if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight) {
                        paint.paintedCells.put(new Point(nx, ny), paint.getActiveColor());
                    }
                }
            }
        } else {
            for (int dy = 0; dy < paint.getBrushSize(); dy++) {
                for (int dx = 0; dx < paint.getBrushSize(); dx++) {
                    int nx = centerX + dx;
                    int ny = centerY + dy;
                    if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight) {
                        paint.paintedCells.put(new Point(nx, ny), paint.getActiveColor());
                    }
                }
            }
        }
        paint.repaint();
    }
}
