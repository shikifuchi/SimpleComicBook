package benedict.zhang.comic.datamodel;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The class for comic book abstraction
 * this class stores information for a comic book in compressed format
 * TODO in the future we will try to implement directory based comic book
 * Created by Benedict on 12/29/15.
 */
public class ComicBook {

    private List<ComicPage> pages;

    private ListProperty<ComicPage> pagesProperty;

    public ComicBook(File comicFile) {
        init(comicFile);
    }


    private void init(File commicFile) {
        List<ComicPage> tmpList = new ArrayList<>();
        ZipInputStream zis = null;
        BufferedInputStream bis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(commicFile));
            //bis = new BufferedInputStream(zis);
            ZipEntry entry = zis.getNextEntry();
            int index = 1;
            while (entry != null) {
                if (!entry.isDirectory() && !entry.getName().contains("MACOSX")) {
                    ComicPage comicPage = new ComicPage(entry.getName(), zis, index);
                    tmpList.add(comicPage);
                    index++;
                }
                entry = zis.getNextEntry();
            }
            Collections.sort(tmpList, new Comparator<ComicPage>() {
                @Override
                public int compare(ComicPage o1, ComicPage o2) {
                    return o1.getPath().compareTo(o2.getPath());
                }
            });
            for (int i = 0; i < tmpList.size(); i++) {
                tmpList.get(i).setIndex(i + 1);
            }
            this.setPages(tmpList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception une) {
            une.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }

                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) {

            }
        }
    }

    public List<ComicPage> getPages() {
        return pages;
    }

    public void setPages(List<ComicPage> pages) {
        this.pages = pages;
        this.pagesProperty = new SimpleListProperty<>(FXCollections.observableArrayList(pages));
    }

    public ListProperty<ComicPage> pagesPropertyProperty() {
        return pagesProperty;
    }
}
