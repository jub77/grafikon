package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Interface for loading/saving train diagrams.
 *
 * @author jub
 */
public interface FileLoadSave {

    TrainDiagram load(File file) throws LSException;

    TrainDiagram load(ZipInputStream is) throws LSException;

    void save(TrainDiagram diagram, File file) throws LSException;

    void save(TrainDiagram diagram, ZipOutputStream os) throws LSException;

    List<ModelVersion> getLoadVersions();

    ModelVersion getSaveVersion();

    Object getProperty(String key);

    void setProperty(String key, Object value);

    LibraryLoadSave getLibraryLoadSave();
}
