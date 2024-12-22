package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.LSException;

import java.net.URL;

public interface DataItemLoader<T> {

    DataItemList loadList() throws LSException;

    T loadItem(DataItem item) throws LSException;

    static <T> DataItemLoader<T> getFromResources(String itemsLocation, String itemListFile, Class<T> clazz) {
        return new ResourceLoader<>(LoadDelegate.createForClass(clazz), itemListFile, itemsLocation);
    }

    static <T> DataItemLoader<T> getFromFiles(String itemsLocation, String itemListFile, Class<T> clazz) {
        return new FileLoader<>(LoadDelegate.createForClass(clazz), itemListFile, itemsLocation);
    }

    static <T> DataItemLoader<T> getFromUrl(URL url, String itemListFile, Class<T> clazz) {
        return new UrlLoader<>(url, itemListFile, LoadDelegate.createForClass(clazz));
    }
}
