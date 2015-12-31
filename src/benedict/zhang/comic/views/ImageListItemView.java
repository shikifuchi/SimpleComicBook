package benedict.zhang.comic.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Class of the complex view in the pages' view
 * display the page image and its page number
 * Created by Benedict on 1/1/16.
 */
public class ImageListItemView extends VBox {

    private Label pageNumber;

    private ImageView imageView;

    public ImageListItemView(Label pageNumber, ImageView imageView) {
        super();
        this.setAlignment(Pos.CENTER);
        this.pageNumber = pageNumber;
        this.imageView = imageView;
        this.getChildren().add(this.imageView);
        this.getChildren().add(this.pageNumber);
    }
}
