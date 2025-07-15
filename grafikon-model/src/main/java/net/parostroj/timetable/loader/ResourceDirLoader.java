package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class for loading items from resources.
 *
 * @author jub
 */
class ResourceDirLoader<T> extends AbstractLoader<T, LSSource> {

    public ResourceDirLoader(LoadDelegate<T, LSSource> loadDelegate, String listFile, String itemLocation) {
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
        return ResourceDirLoader.class.getResourceAsStream(itemLocation + "/" + itemListFile);
    }

    @Override
    protected LSSource getItemSource(DataItem item) throws IOException {
        try {
            return LSSource.createFromResourceDir(itemLocation + "/" + item.filename());
        } catch (LSException e) {
            if (e.getCause() instanceof IOException ioe) {
                throw ioe;
            }
            throw new IOException(e);
        }
    }
}
