package zh.ppt.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Presentation implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<PageData> pagesData;

    public Presentation() {
        pagesData = new ArrayList<>();
    }

    public void addPageData(PageData pageData) {
        pagesData.add(pageData);
    }

    public ArrayList<PageData> getPagesData() {
        return pagesData;
    }

    public void clear() {
        pagesData.clear();
    }
}
