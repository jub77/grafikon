package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.TrainDiagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
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
        try {
            return new ZipFileSink(file);
        } catch (FileNotFoundException e) {
            throw new LSException(e);
        }
    }

    static LSSink createForDir(File file) {
        return new DirectorySink(file);
    }
}
