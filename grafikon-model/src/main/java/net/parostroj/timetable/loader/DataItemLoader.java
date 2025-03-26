package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.LSException;

import java.net.URL;
import java.util.function.Function;

public interface DataItemLoader<T> {

    DataItemList loadList() throws LSException;

    T loadItem(DataItem item) throws LSException;

    default <V> DataItemLoader<V> transform(Function<T, V> tf) {
        return new DataItemLoader<V>() {
            @Override
            public DataItemList loadList() throws LSException {
                return DataItemLoader.this.loadList();
            }

            @Override
            public V loadItem(DataItem item) throws LSException {
                return tf.apply(DataItemLoader.this.loadItem(item));
            }
        };
    }

    static <T> DataItemLoader<T> getFromResources(String itemsLocation, String itemListFile, LoadDelegate<T> delegate) {
        return new ResourceLoader<>(delegate, itemListFile, itemsLocation);
    }

    static <T> DataItemLoader<T> getFromFiles(String itemsLocation, String itemListFile, LoadDelegate<T> delegate) {
        return new FileLoader<>(delegate, itemListFile, itemsLocation);
    }

    static <T> DataItemLoader<T> getFromUrl(URL url, String itemListFile, LoadDelegate<T> delegate) {
        return new UrlLoader<>(url, itemListFile, delegate);
    }
}
