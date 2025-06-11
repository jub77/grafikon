package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.TrainDiagram;

import java.io.*;
import java.nio.file.Path;
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
            if (file.exists() && file.isDirectory()) {
                return new DirectorySource(file);
            } else {
                return new ZipFileSource(file);
            }
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    static LSSource create(Path path) throws LSException {
        return create(path.toFile());
    }
}
