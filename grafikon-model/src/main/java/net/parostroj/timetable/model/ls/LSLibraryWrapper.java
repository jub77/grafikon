package net.parostroj.timetable.model.ls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItem;

/**
 * Wrapper that allows only load operation to be called.
 *
 * @author jub
 */
class LSLibraryWrapper implements LSLibrary {

    private final LSLibrary impl;

    public LSLibraryWrapper(LSLibrary impl) {
        this.impl = impl;
    }

    @Override
    public void saveItem(LibraryItem item, OutputStream os) throws LSException {
        throw new LSException("Save operation not supported.");
    }

    @Override
    public LibraryItem loadItem(TrainDiagramType diagramType, InputStream is) throws LSException {
        return impl.loadItem(diagramType, is);
    }

    @Override
    public void save(Library library, ZipOutputStream os) throws LSException {
        throw new LSException("Save operation not supported.");
    }

    @Override
    public Library load(TrainDiagramType diagramType, ZipInputStream is) throws LSException {
        return impl.load(diagramType, is);
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
