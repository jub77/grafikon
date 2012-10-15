package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Wrapper that allows only load operation to be called.
 *
 * @author jub
 */
public class FileLoadSaveLoadWrapper implements FileLoadSave {
    
    private final FileLoadSave impl;

    public FileLoadSaveLoadWrapper(FileLoadSave impl) {
        this.impl = impl;
    }

    @Override
    public TrainDiagram load(File file) throws LSException {
        return impl.load(file);
    }

    @Override
    public TrainDiagram load(ZipInputStream is) throws LSException {
        return impl.load(is);
    }

    @Override
    public void save(TrainDiagram diagram, File file) throws LSException {
        throw new LSException("Save operation not supported.");
    }

    @Override
    public void save(TrainDiagram diagram, ZipOutputStream os) throws LSException {
        throw new LSException("Save operation not supported.");
    }

    @Override
    public List<ModelVersion> getLoadVersions() {
        return impl.getLoadVersions();
    }

    @Override
    public ModelVersion getSaveVersion() {
        return impl.getSaveVersion();
    }
}
