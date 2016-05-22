package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.TrainDiagram;

/**
 * Interface for saving train diagrams.
 *
 * @author jub
 */
public interface LSFile extends LSVersions {

    TrainDiagram load(File file) throws LSException;

    TrainDiagram load(ZipInputStream is) throws LSException;

    Object getProperty(String key);

    void setProperty(String key, Object value);

    void save(TrainDiagram diagram, File file) throws LSException;

    void save(TrainDiagram diagram, ZipOutputStream os) throws LSException;
}
