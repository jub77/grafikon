package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.TrainDiagram;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;

public interface LSSink extends AutoCloseable {

    default void updateInfo(TrainDiagram diagram) {
        // nothing
    }

    OutputStream nextItem(String name) throws LSException;

    @Override
    default void close() throws LSException {
        // nothing
    }

    static LSSink create(ZipOutputStream zos) {
        return new ZipStreamSink(zos);
    }

    static LSSink create(File file) throws LSException {
        if (file.exists() && file.isDirectory()) {
            return new DirectorySink(file);
        } else {
            try {
                return new ZipFileSink(file);
            } catch (FileNotFoundException e) {
                throw new LSException(e);
            }
        }
    }

    static LSSink create(Path path) throws LSException {
        return create(path.toFile());
    }
}
