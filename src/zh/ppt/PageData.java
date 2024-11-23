package zh.ppt;

import java.io.Serializable;
import java.util.ArrayList;

public class PageData implements Serializable {

    private ArrayList<TextBoxData> textBoxes;
    private ArrayList<ShapeData> shapes;
    private ArrayList<ImageData> images;

    public PageData() {
        textBoxes = new ArrayList<>();
        shapes = new ArrayList<>();
        images = new ArrayList<>();
    }

    public void addTextBoxData(TextBoxData textBoxData) {
        textBoxes.add(textBoxData);
    }

    public ArrayList<TextBoxData> getTextBoxes() {
        return textBoxes;
    }

    // 形状相关方法
    public void addShapeData(ShapeData shapeData) {
        shapes.add(shapeData);
    }

    public ArrayList<ShapeData> getShapes() {
        return shapes;
    }

    // 图片相关方法
    public void addImageData(ImageData imageData) {
        images.add(imageData);
    }

    public ArrayList<ImageData> getImages() {
        return images;
    }

}
