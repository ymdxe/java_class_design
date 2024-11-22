package zh.ppt;

import java.io.Serializable;
import java.util.ArrayList;

public class PageData implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<TextBoxData> textBoxes;

    public PageData() {
        textBoxes = new ArrayList<>();
    }

    public void addTextBoxData(TextBoxData textBoxData) {
        textBoxes.add(textBoxData);
    }

    public ArrayList<TextBoxData> getTextBoxes() {
        return textBoxes;
    }

    public void setTextBoxes(ArrayList<TextBoxData> textBoxes) {
        this.textBoxes = textBoxes;
    }
}
