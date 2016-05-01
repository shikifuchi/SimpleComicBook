package benedict.zhang.comic.views;

import benedict.zhang.comic.common.SceneManager;
import benedict.zhang.comic.common.UIKey;
import benedict.zhang.comic.datamodel.ComicBook;
import benedict.zhang.comic.datamodel.ComicPage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.sql.Time;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainUIController implements Initializable {


    private static final Integer LIST_VIEW_WIDTH = 225;

    @FXML
    private ListView<ImageListItemView> allImagesListView;

    @FXML
    private ImageView pageView;

    @FXML
    private Label pageNumber;

    @FXML
    private MenuItem cancelAuto;

    private Stage _owner;

    private ComicBook comicBook;

    // current page of the comic to display on the ui
    private ComicPage currentPage;

    private SimpleDoubleProperty imageViewHeight;

    private SimpleDoubleProperty imageViewWidth;

    private SimpleBooleanProperty autoMode;

    private Timeline autoModeTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _owner = SceneManager.getInstance().loadStage(UIKey.MAIN);
        autoMode = new SimpleBooleanProperty(false);
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
                    gotoPage(newValue.intValue() + 1, false);
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
            updateImageView();
            pageView.imageProperty().bindBidirectional(currentPage.getImageProperty());
            _owner.widthProperty().addListener((observable, oldValue, newValue) -> {
                updateImageView();
            });
            _owner.heightProperty().addListener((observable, oldValue, newValue) -> {
                updateImageView();
            });
        }
        autoMode.addListener((observable, oldValue, newValue) -> {
            if (autoModeTimer == null) { // init timer
                autoModeTimer = new Timeline(new KeyFrame(Duration.seconds(8), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        next();
                    }
                }));
            }
            if (newValue) {
                autoModeTimer.setCycleCount(Timeline.INDEFINITE);
                autoModeTimer.play();
            } else {
                autoModeTimer.stop();
            }
        });

        cancelAuto.visibleProperty().bindBidirectional(autoMode);
    }

    private void updateImageView() {
        if (imageViewWidth == null) {
            imageViewWidth = new SimpleDoubleProperty(0.0);
            pageView.fitWidthProperty().bindBidirectional(imageViewWidth);
        }
        if (imageViewHeight == null) {
            imageViewHeight = new SimpleDoubleProperty(0.0);
            pageView.fitHeightProperty().bindBidirectional(imageViewHeight);
        }
        calculateScaledRatio();

    }

    private void calculateScaledRatio() {
        Double scaledRatio = 1.0;
        Double imageWidth = currentPage.getCommicPageFile().getWidth();
        Double imageHeight = currentPage.getCommicPageFile().getHeight();
        Double appWidth = SceneManager.getInstance().loadStage(UIKey.MAIN).getWidth() - 200;
        Double appHeight = SceneManager.getInstance().loadStage(UIKey.MAIN).getHeight() - 200;
        if (imageHeight > appHeight || imageWidth > appWidth) { // resize the image if it is too large to display
            scaledRatio = (appWidth / imageWidth) < (appHeight / imageHeight) ? appWidth / imageWidth : appHeight / imageHeight;
        }
        imageViewHeight.set(imageHeight * scaledRatio);
        imageViewWidth.set(imageWidth * scaledRatio);
    }

    private void buildImageViewList() {
        allImagesListView.getItems().clear();
        for (ComicPage page : comicBook.getPages()) {
            Image image = page.getCommicPageFile();
            ImageView imageView = new ImageView();
            Label pageNumber = new Label();
            imageView.setFitWidth(LIST_VIEW_WIDTH);
            Double imageHeight = image.getHeight();
            if (image.getWidth() > LIST_VIEW_WIDTH) {
                Double scaledRatio = LIST_VIEW_WIDTH / image.getWidth();
                imageHeight = imageHeight * scaledRatio;
            }
            imageView.setFitHeight(imageHeight);
            imageView.setImage(image);
            pageNumber.setText(page.getIndexProperty().getValue());
            ImageListItemView itemView = new ImageListItemView(pageNumber, imageView);
            allImagesListView.getItems().add(page.getIndex() - 1, itemView);
        }
        allImagesListView.refresh();
    }

    public void openComic(ActionEvent event) {
        autoMode.setValue(false);
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
        next();
    }

    public void auto(ActionEvent event) {
        autoMode.setValue(true);
    }

    public void cancelAuto(ActionEvent event) {
        autoMode.setValue(false);
    }

    private void next() {
        Integer index = currentPage.getIndex();
        index++;
        gotoPage(index, true);
    }

    /**
     * Method to do previous action
     * to display the next page
     * and the selected page on the list view should be update
     */
    public void previous(ActionEvent event) {
        previous();
    }

    private void previous() {
        Integer index = currentPage.getIndex();
        if (index == 1) return;
        index--;
        gotoPage(index, true);
    }

    /**
     * 2016/01/01 change api
     *
     * @param index    the page number
     *                 NOT the true index in the list
     *                 the true index = page number - 1
     * @param scrollTo if scroll to the select page
     *                 this new parameter is added to indicate whether to scroll to the select page
     *                 when the page is selected by the user manually, it is false
     *                 if the page is selected because user click the navigate buttons or <- or ->  button it is true
     */
    private void gotoPage(Integer index, Boolean scrollTo) {
        index = index < 1 ? 1 : index;
        for (int i = 0; i < comicBook.getPages().size(); i++) {
            ComicPage page = comicBook.getPages().get(i);
            if (Objects.equals(page.getIndex(), index)) {
                currentPage.setPath(page.getPath());
                currentPage.setIndex(page.getIndex());
                currentPage.setCommicPageFile(page.getCommicPageFile());
                // TODO other operations when current page is changed
                allImagesListView.getSelectionModel().select(index - 1);
                if (scrollTo)
                    allImagesListView.scrollTo(index - 1);
                updateImageView();
                break;
            }
        }
    }


    /**
     * Method to go to the top page of the comic book
     * the selected page on the list view should be update
     */
    public void top(ActionEvent event) {
        gotoPage(1, true);
    }

    public void ShortCutKeyAction(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT) next();
        else if (event.getCode() == KeyCode.LEFT) previous();
    }


    class BookLoadingTask<ComicBook> extends Task {

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
            _owner.setTitle(oriTitle);
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
