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

    public TrainDiagram load(File file) throws LSException;

    public TrainDiagram load(ZipInputStream is) throws LSException;

    public void save(TrainDiagram diagram, File file) throws LSException;

    public void save(TrainDiagram diagram, ZipOutputStream os) throws LSException;

    public List<ModelVersion> getLoadVersions();

    public ModelVersion getSaveVersion();

    public Object getProperty(String key);

    public void setProperty(String key, Object value);
}
