package net.parostroj.timetable.loader;

import java.io.InputStream;

/**
 * Class for loading items from resources.
 *
 * @author jub
 */
class ResourceLoader<T> extends AbstractLoader<T> {

    public ResourceLoader(LoadDelegate<T> loadDelegate, String listFile, String itemLocation) {
        super(loadDelegate);
        this.itemListFile = listFile;
        this.itemLocation = itemLocation;
    }

    private final String itemListFile;
    private final String itemLocation;

    @Override
    public String toString() {
        return "Resource[" + itemLocation + "/" + itemListFile + "]";
    }

    @Override
    protected InputStream getItemListStream() {
        return ResourceLoader.class.getResourceAsStream(itemLocation + "/" + itemListFile);
    }

    @Override
    protected InputStream getItemStream(DataItem item) {
        return ResourceLoader.class.getResourceAsStream(itemLocation + "/" + item.filename());
    }
}
