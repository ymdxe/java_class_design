package zh.ppt;

import java.io.Serializable;

public class ImageData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String imagePath; // 图片路径
    private int x, y, width, height;

    public ImageData(String imagePath, int x, int y, int width, int height) {
        this.imagePath = imagePath;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Getters and setters
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
}
