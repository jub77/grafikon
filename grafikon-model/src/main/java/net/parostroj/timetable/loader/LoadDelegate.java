package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.LSException;

import java.io.InputStream;

@FunctionalInterface
public interface LoadDelegate<T> {

    T load(InputStream is, DataItem dataItem) throws LSException;
}
