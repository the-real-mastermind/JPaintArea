import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.LineBorder;

/**
 * A JComponent that allows the user to paint onto a canvas
 * */
public class JPaintArea extends JPanel {

    /**
     * The amount of paintable cells on the canvas
     * */
    private int cellX, cellY;

    /**
     * Used for calculations
     * */
    protected int gridWidth, gridHeight;

    /**
     * The size (in cells) of the brush that the user uses to paint on the paint area
     * */
    private int brushSize = 1;

    /**
     * The size that each cells takes up on the frame
     * */
    int cellWidth, cellHeight;

    /**
     * how many cells are there between each grid (grid is locked to a square)
     * */
    private int gridSize = 16;

    /**
     * Does the paint area display the grid
     * */
    private boolean useGrid;

    /**
     * Hashmap of the all the painted cells and their color
     * */
    protected Map<Point, Color> paintedCells = new HashMap<>();

    /**
     * The cell the user is hovering over
     * */
    protected Point hoveredCell = null;

    /**
     * The active color used for painting on cells
     * */
    private Color activeColor = Color.black;

    /**
     * The color of the grid
     * */
    private Color gridColor = new Color(0xFFFFFFF);



    /**
     * The mouse event listener for the paint area
     * */
    public listener listener = new listener();

    /**
     * Creates a Paint area with a set cell size and size for the paint area. Does not have a grid by default.
     * @param cellX the amount of cells on the X axis
     * @param cellY the amount of cells on the Y axis
     * @param panelWidth the width of the JPaintArea
     * @param panelHeight the height of the JPaintArea
     * */
    public JPaintArea(int cellX, int cellY, int panelWidth, int panelHeight) {
        this.cellX = cellX;
        this.cellY = cellY;

        this.gridWidth = cellX;
        this.gridHeight = cellY;

        this.cellWidth = panelWidth / gridWidth;
        this.cellHeight = panelHeight / gridHeight;

        int exactWidth = cellWidth * gridWidth;
        int exactHeight = cellHeight * gridHeight;
        this.setPreferredSize(new Dimension(exactWidth, exactHeight));

        //this.setBorder(new LineBorder(gridColor, 3));
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);

        listener.paint = this;
        addMouseListener(listener);
        addMouseMotionListener(listener);
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

                if (useGrid) {
                    g.setColor(gridColor);
                    g.drawRect((x * cellWidth) * gridSize, (y * cellHeight) * gridSize, (cellWidth) * gridSize , (cellHeight) * gridSize);
                }
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
     * Sets the size of a single grid cell
     * @param size the new cell size as a {@link Dimension} object
     */
    public void setCellSize(Dimension size) {
        this.cellX = size.width;
        this.cellY = size.height;
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

}

/**
 * The mouse listener for the JPaintArea
 * */
class listener extends MouseAdapter {
    public JPaintArea paint;
    public boolean held;
    private Point lastPaintedCell = null;

    @Override
    public void mousePressed(MouseEvent e) {
        held = true;
        paintCell(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        held = false;
        lastPaintedCell = null;
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
