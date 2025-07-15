package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.LSException;

import java.io.InputStream;

@FunctionalInterface
public interface LoadDelegate<T, I> {

    T load(I is, DataItem dataItem) throws LSException;
}
