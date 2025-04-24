package net.parostroj.timetable.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Class for loading items from resources.
 *
 * @author jub
 */
class FileLoader<T> extends AbstractLoader<T> {

    public FileLoader(LoadDelegate<T> loadDelegate, String listFile, String itemLocation) {
        super(loadDelegate);
        this.itemListFile = listFile;
        this.itemLocation = itemLocation;
    }

    private final String itemListFile;
    private final String itemLocation;

    @Override
    public String toString() {
        return "File[" + itemLocation + "/" + itemListFile + "]";
    }

    @Override
    protected InputStream getItemListStream() throws FileNotFoundException {
        return new FileInputStream(itemLocation + "/" + itemListFile);
    }

    @Override
    protected InputStream getItemStream(DataItem item) throws FileNotFoundException {
        return new FileInputStream(itemLocation + "/" + item.filename());
    }
}
