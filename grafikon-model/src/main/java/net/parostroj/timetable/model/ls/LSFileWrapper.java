package net.parostroj.timetable.model.ls;

import java.util.List;
import net.parostroj.timetable.model.TrainDiagram;

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
    public TrainDiagram load(LSSource source, LSFeature... features) throws LSException {
        return impl.load(source, features);
    }

    @Override
    public void save(TrainDiagram diagram, LSSink sink) throws LSException {
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
