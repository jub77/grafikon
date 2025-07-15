package net.parostroj.timetable.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Loading of items from base url.
 *
 * @author jub
 */
class UrlLoader<T> extends AbstractLoader<T, InputStream> {

    private final URL baseUrl;
    private final String itemListFile;

    public UrlLoader(URL baseUrl, String itemListFile, LoadDelegate<T, InputStream> loadDelegate) {
        super(loadDelegate);
        this.baseUrl = baseUrl;
        this.itemListFile = itemListFile;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    protected InputStream getItemListStream() throws IOException {
        return URI.create(baseUrl.toString() + "/" + itemListFile).toURL().openStream();
    }

    @Override
    protected InputStream getItemSource(DataItem item) throws IOException {
        return URI.create(baseUrl.toString() + "/" + item.filename()).toURL().openStream();
    }

    @Override
    public String toString() {
        return String.format("Url[%s]", baseUrl.toString() + "/" + itemListFile);
    }
}
