package benedict.zhang.comic.common;

import benedict.zhang.comic.datamodel.ComicBook;
import benedict.zhang.comic.datamodel.ComicPage;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to manage all scenes used in this application
 * Created by Benedict on 12/27/15.
 */
public class SceneManager {

    private static SceneManager _instance;

    private Map<String, Stage> sceneMap;

    public static Map<String, String> uiRegistryMap = new HashMap<>();

    private ComicBook currentBook;

    private ComicPage currentPage;

    public ComicBook getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(ComicBook currentBook) {
        this.currentBook = currentBook;
    }

    public ComicPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(ComicPage currentPage) {
        this.currentPage = currentPage;
    }

    static {
        uiRegistryMap.put(UIKey.MAIN, "views/main.fxml");
    }

    private SceneManager() {
        sceneMap = new HashMap<>();
    }

    /**
     * The method to get the singleton instance of scene manager
     *
     * @return SceneManager
     */
    public static SceneManager getInstance() {
        if (_instance == null) {
            _instance = new SceneManager();
        }
        return _instance;
    }

    /**
     * The method used to store the scene
     *
     * @param key
     * @param stage
     * @return Scene the scene store in the registry
     */
    public Stage registStage(String key, Stage stage) {
        return this.sceneMap.put(key, stage);
    }

    public Stage loadStage(String key) {
        return this.sceneMap.get(key);
    }


}
