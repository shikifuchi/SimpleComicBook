package benedict.zhang.comic;

import benedict.zhang.comic.common.SceneManager;
import benedict.zhang.comic.common.UIKey;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // default width of main ui
    private static final Integer DEFAULT_WIDTH = 1280;

    // default height of main ui
    private static final Integer DEFAULT_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeApplicationConfig();
        primaryStage.setScene(initializeApplicationUI());
        primaryStage.setTitle("Simple Comic Books Reader - Programmed by Benedict");
        SceneManager.getInstance().registStage(UIKey.MAIN, primaryStage);
        primaryStage.show();
    }


    /**
     * Method to load configuration files
     */
    protected void initializeApplicationConfig() {

    }

    /**
     * Method to init ui component
     */
    protected Scene initializeApplicationUI() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(SceneManager.uiRegistryMap.get(UIKey.MAIN)));
        Scene primaryScene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        return primaryScene;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
