package zh.ppt;

import java.awt.*;
import java.io.Serializable;

public class TextBoxData implements Serializable {

    private String textContent;
    private int x, y, width, height;
    private String fontName;
    private int fontStyle;
    private int fontSize;
    private Color textColor;

    public TextBoxData(String textContent, int x, int y, int width, int height,
                       String fontName, int fontStyle, int fontSize, Color textColor) {
        this.textContent = textContent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fontName = fontName;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        this.textColor = textColor;
    }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public String getFontName() { return fontName; }
    public void setFontName(String fontName) { this.fontName = fontName; }

    public int getFontStyle() { return fontStyle; }
    public void setFontStyle(int fontStyle) { this.fontStyle = fontStyle; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }

    public Color getTextColor() { return textColor; }
    public void setTextColor(Color textColor) { this.textColor = textColor; }
}
