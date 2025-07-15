package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.TrainDiagram;

import java.io.*;
import java.util.zip.ZipInputStream;

public interface LSSource extends AutoCloseable {

    record Item(String name, InputStream stream) {}

    default void updateInfo(TrainDiagram diagram) {
        // nothing
    }

    Item nextItem() throws LSException;

    @Override
    default void close() throws LSException {
        // nothing
    }

    static LSSource create(ZipInputStream zis) {
        return new ZipStreamSource(zis);
    }

    static LSSource create(File file) throws LSException {
        try {
            return new ZipFileSource(file);
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    static LSSource createFromDir(File file) throws LSException {
        try {
            return new DirectorySource(file);
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    static LSSource createFromResourceDir(String resourcePath) throws LSException {
        try {
            return new ResourceDirectorySource(resourcePath);
        } catch (IOException e) {
            throw new LSException(e);
        }
    }
}
