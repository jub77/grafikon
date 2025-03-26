package net.parostroj.timetable.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.parostroj.timetable.model.ls.LSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

abstract class AbstractLoader<T> implements DataItemLoader<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractLoader.class);

    private DataItemList itemList;
    private final LoadDelegate<T> loadDelegate;

    AbstractLoader(LoadDelegate<T> loadDelegate) {
        this.loadDelegate = loadDelegate;
    }

    @Override
    public DataItemList loadList() throws LSException {
        if (itemList == null) {
            // load item list
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try (InputStream is = getItemListStream()) {
                itemList = mapper.readValue(is, DataItemList.class);
                log.debug("Loaded list of items: {}", this);
            } catch (IOException e) {
                throw new LSException("Error reading list of items: " + e.getMessage(), e);
            }
        }
        return itemList;
    }

    protected abstract InputStream getItemListStream() throws IOException;

    @Override
    public T loadItem(DataItem item) throws LSException {
        // if no filename is defined - return null
        if (item.filename() == null) {
            return null;
        }
        // create file with item location
        T instance;
        try (InputStream is = getItemStream(item)) {
            instance = loadDelegate.load(is, item);
        } catch (IOException e) {
            throw new LSException("Error loading item: " + e.getMessage(), e);
        }
        return instance;
    }

    protected abstract InputStream getItemStream(DataItem item) throws IOException;
}
