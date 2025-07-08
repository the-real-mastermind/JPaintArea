import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import javax.swing.border.LineBorder;

public class PaintArea extends JPanel {
    private int scaleX, scaleY; // number of cells in one direction (width and height of the grid in cells)
    protected int gridWidth, gridHeight; // number of cells (same as scale)

    private int brushSize = 1;
    private boolean useGrid;

    protected Map<Point, Color> paintedCells = new HashMap<>();
    protected Point hoveredCell = null;
    private Color activeColor = Color.black;
    private Color gridColor = new Color(0xF0E0FF);

    private final int panelWidth, panelHeight;
    int cellWidth;
    int cellHeight;

    private int gridSize = 16;

    public listener listener = new listener();

    public PaintArea(int pixelX, int pixelY, int panelWidth, int panelHeight) {
        this.scaleX = pixelX;
        this.scaleY = pixelY;

        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        this.gridWidth = pixelX;
        this.gridHeight = pixelY;

        // Compute cell size with integer division (may leave remainder pixels)
        this.cellWidth = panelWidth / gridWidth;
        this.cellHeight = panelHeight / gridHeight;

        // Set size to exactly the grid's pixel size, hiding leftover area
        int exactWidth = cellWidth * gridWidth;
        int exactHeight = cellHeight * gridHeight;
        this.setPreferredSize(new Dimension(exactWidth, exactHeight));

        this.setBorder(new LineBorder(gridColor, 3));
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);

        listener.paint = this;
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

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

        // Hover effect
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

            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(rectX, rectY, rectW, rectH);
            g.setColor(Color.WHITE);
            g.drawRect(rectX, rectY, rectW - 1, rectH - 1);
        }
    }


    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }


    public int getBrushSize() {
        return brushSize;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public Dimension getSize() {
        return new Dimension(scaleX, scaleY);
    }

    public void setScaleX(Dimension size) {
        this.scaleX = size.width;
        this.scaleY = size.height;

    }

    public boolean isUseGrid() {
        return useGrid;
    }

    public void setUseGrid(boolean useGrid) {
        this.useGrid = useGrid;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
    }
}


    //Events and such
class listener extends MouseAdapter {
    public PaintArea paint;
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
        lastPaintedCell = null; // reset
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
