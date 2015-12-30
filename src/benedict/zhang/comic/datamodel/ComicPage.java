package benedict.zhang.comic.datamodel;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Benedict on 12/29/15.
 */
public class ComicPage {

    // imageFile
    private Image commicPageFile;

    // path in the zip file
    private String path;

    // index in the book
    private Integer index;

    private ObjectProperty<Image> imageProperty;

    private StringProperty indexProperty;

    public ComicPage(String path, InputStream is, Integer index) {
        this.commicPageFile = new Image(is);
        this.path = path;
        this.index = index;
        imageProperty = new SimpleObjectProperty<>(commicPageFile);
        indexProperty = new SimpleStringProperty(index.toString());
    }

    public ComicPage(ComicPage page) {
        this.commicPageFile = page.getCommicPageFile();
        this.path = page.getPath();
        this.index = page.getIndex();
        imageProperty = new SimpleObjectProperty<>(commicPageFile);
        indexProperty = new SimpleStringProperty(index.toString());
    }


    public ObjectProperty<Image> getImageProperty() {
        return imageProperty;
    }

    public StringProperty getIndexProperty() {
        return indexProperty;
    }

    public Image getCommicPageFile() {
        return commicPageFile;
    }

    public void setCommicPageFile(Image commicPageFile) {
        this.commicPageFile = commicPageFile;
        imageProperty.setValue(commicPageFile);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        indexProperty.setValue(index.toString());
    }
}
