package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.ls.LSException;

import java.net.URL;

public interface DataItemLoader<T> {

    DataItemList loadList() throws LSException;

    T loadItem(DataItem item) throws LSException;

    static <T> DataItemLoader<T> getFromResources(TrainDiagramType type, String itemsLocation, String itemListFile, Class<T> clazz) {
        return new ResourceLoader<>(LoadDelegate.createForClass(type, clazz), itemListFile, itemsLocation);
    }

    static <T> DataItemLoader<T> getFromFiles(TrainDiagramType type, String itemsLocation, String itemListFile, Class<T> clazz) {
        return new FileLoader<>(LoadDelegate.createForClass(type, clazz), itemListFile, itemsLocation);
    }

    static <T> DataItemLoader<T> getFromUrl(TrainDiagramType type, URL url, String itemListFile, Class<T> clazz) {
        return new UrlLoader<>(url, itemListFile, LoadDelegate.createForClass(type, clazz));
    }
}
