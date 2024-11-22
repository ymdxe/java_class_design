package zh.ppt.data;

import java.awt.Color;
import java.io.Serializable;

public class ShapeData implements Serializable {

    public static final int TYPE_LINE = 0;
    public static final int TYPE_RECTANGLE = 1;
    public static final int TYPE_CIRCLE = 2;
    public static final int TYPE_ELLIPSE = 3;

    private int shapeType;
    private int x, y, width, height;
    private Color fillColor;
    private Color borderColor;


    public ShapeData(int shapeType, int x, int y, int width, int height, Color fillColor, Color borderColor) {
        this.shapeType = shapeType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
    }

    public int getShapeType() { return shapeType; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public Color getFillColor() { return fillColor; }
    public void setFillColor(Color fillColor) { this.fillColor = fillColor; }

    public Color getBorderColor() { return borderColor; }

}
