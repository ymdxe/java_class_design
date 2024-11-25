package zh.ppt.data;

import java.io.Serializable;

public class ImageData implements Serializable {

    private String imagePath; // 图片路径
    private int x, y, width, height;

    public ImageData(String imagePath, int x, int y, int width, int height) {
        this.imagePath = imagePath;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getImagePath() { return imagePath; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }
}
