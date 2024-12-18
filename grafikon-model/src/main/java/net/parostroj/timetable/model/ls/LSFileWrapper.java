package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;

/**
 * Wrapper that allows only load operation to be called.
 *
 * @author jub
 */
class LSFileWrapper implements LSFile {

    private final LSFile impl;

    public LSFileWrapper(LSFile impl) {
        this.impl = impl;
    }

    @Override
    public TrainDiagram load(TrainDiagramType diagramType, File file) throws LSException {
        return impl.load(diagramType, file);
    }

    @Override
    public TrainDiagram load(TrainDiagramType diagramType, ZipInputStream is) throws LSException {
        return impl.load(diagramType, is);
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

    @Override
    public Object getProperty(String key) {
        return impl.getProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        impl.setProperty(key, value);
    }
}
