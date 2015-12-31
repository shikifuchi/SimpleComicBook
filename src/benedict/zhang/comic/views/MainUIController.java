package benedict.zhang.comic.views;

import benedict.zhang.comic.common.SceneManager;
import benedict.zhang.comic.common.UIKey;
import benedict.zhang.comic.datamodel.ComicBook;
import benedict.zhang.comic.datamodel.ComicPage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainUIController implements Initializable {


    private static final Integer LIST_VIEW_WIDTH = 225;

    @FXML
    private ListView<ImageView> allImagesListView;

    @FXML
    private ImageView pageView;

    @FXML
    private TextField pageNumber;

    private Stage _owner;

    private ComicBook comicBook;

    // current page of the comic to display on the ui
    private ComicPage currentPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _owner = SceneManager.getInstance().loadStage(UIKey.MAIN);
        bindUI();
    }

    private void bindUI() {
        /**
         *
         * New feature 2016/01/01
         * change the current page when select page in list view is changed
         * Begin
         *
         * */
        allImagesListView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    gotoPage(newValue.intValue() + 1);
                });
        /**
         *
         * End
         *
         * */
        if (comicBook != null) if (comicBook.getPages().size() > 0) {
            currentPage = new ComicPage(comicBook.getPages().get(0));
            buildImageViewList();
        }
        if (currentPage != null) {
            pageNumber.textProperty().bindBidirectional(currentPage.getIndexProperty());
            pageNumber.textProperty().addListener((observable, oldValue, newValue) -> {
                // change the text will cause current page being selected
            });
            Double imageWidth = currentPage.getCommicPageFile().getWidth();
            Double imageHeight = currentPage.getCommicPageFile().getHeight();
            Double appWidth = SceneManager.getInstance().loadStage(UIKey.MAIN).getWidth() - 300;
            Double appHeight = SceneManager.getInstance().loadStage(UIKey.MAIN).getHeight() - 200;
            if (imageHeight > appHeight || imageWidth > appHeight) { // resize the image if it is too large to display
                Double scaledRatio = appWidth / imageWidth < appHeight / imageHeight ? appWidth / imageWidth : appHeight / imageHeight;
                imageWidth = imageWidth * scaledRatio;
                imageHeight = imageHeight * scaledRatio;
            }
            pageView.fitWidthProperty().bindBidirectional(new SimpleDoubleProperty(imageWidth));
            pageView.fitHeightProperty().bindBidirectional(new SimpleDoubleProperty(imageHeight));
            pageView.imageProperty().bindBidirectional(currentPage.getImageProperty());

        }
    }

    private void buildImageViewList() {
        for (ComicPage page : comicBook.getPages()) {
            Image image = page.getCommicPageFile();
            ImageView imageView = new ImageView();
            imageView.setFitWidth(LIST_VIEW_WIDTH);
            Double imageHeight = image.getHeight();
            if (image.getWidth() > LIST_VIEW_WIDTH) {
                Double scaledRatio = LIST_VIEW_WIDTH / image.getWidth();
                imageHeight = imageHeight * scaledRatio;
            }
            imageView.setFitHeight(imageHeight);
            imageView.setImage(image);
            allImagesListView.getItems().add(page.getIndex() - 1, imageView);
        }
        allImagesListView.refresh();
    }

    public void openComic(ActionEvent event) {
        if (_owner == null) {
            _owner = SceneManager.getInstance().loadStage(UIKey.MAIN);
        }
        FileChooser openFile = new FileChooser();
        File comicFile = openFile.showOpenDialog(_owner);
        if (comicFile != null) {
            try {
                Task loadingTask = new BookLoadingTask<>(comicFile);
                new Thread(loadingTask).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to do next action
     * to display the next page
     * and the selected page on the list view should be update
     */
    public void next(ActionEvent event) {
        Integer index = currentPage.getIndex();
        index++;
        gotoPage(index);
    }

    /**
     * Method to do previous action
     * to display the next page
     * and the selected page on the list view should be update
     */
    public void previous(ActionEvent event) {
        Integer index = currentPage.getIndex();
        if (index == 1) return;
        index--;
        gotoPage(index);

    }

    private void gotoPage(Integer index) {
        index = index < 1 ? 1 : index;
        for (int i = 0; i < comicBook.getPages().size(); i++) {
            ComicPage page = comicBook.getPages().get(i);
            if (Objects.equals(page.getIndex(), index)) {
                currentPage.setPath(page.getPath());
                currentPage.setIndex(page.getIndex());
                currentPage.setCommicPageFile(page.getCommicPageFile());
                // TODO other operations when current page is changed
                allImagesListView.getSelectionModel().select(index - 1);
                allImagesListView.scrollTo(index - 1);
                break;
            }
        }
    }


    /**
     * Method to go to the top page of the comic book
     * the selected page on the list view should be update
     */
    public void top(ActionEvent event) {
        gotoPage(1);
    }


    class BookLoadingTask<ixComicBook> extends Task {

        private File comicFile;

        private String oriTitle;

        private String loadingTitle;

        private String failedTitle;

        public BookLoadingTask(File comicFile) {
            this.comicFile = comicFile;
            oriTitle = _owner.getTitle();
            loadingTitle = oriTitle + " Loading....";
            failedTitle = oriTitle + " Failed";
        }

        @Override
        protected benedict.zhang.comic.datamodel.ComicBook call() throws Exception {
            comicBook = new benedict.zhang.comic.datamodel.ComicBook(comicFile);
            return comicBook;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            _owner.setTitle(oriTitle);
            bindUI();
        }

        @Override
        protected void failed() {
            super.failed();
            _owner.setTitle(failedTitle);
        }

        @Override
        protected void cancelled() {
            super.cancelled();
        }

        @Override
        protected void running() {
            super.running();
            _owner.setTitle(loadingTitle);
        }

    }
}
