package net.parostroj.timetable.model.ls.impl4;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItem;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSLibrary;
import net.parostroj.timetable.model.ls.ModelVersion;

public class LSLibraryImpl extends AbstractLSImpl implements LSLibrary {

    private static final ModelVersion CURRENT_VERSION;
    private static final List<ModelVersion> VERSIONS;

    static {
        VERSIONS = getVersions("1.0");
        CURRENT_VERSION = getLatestVersion(VERSIONS);
    }

    @Override
    public List<ModelVersion> getLoadVersions() {
        return VERSIONS;
    }

    @Override
    public ModelVersion getSaveVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public void saveItem(LibraryItem item, OutputStream os) throws LSException {
    }

    @Override
    public LibraryItem loadItem(InputStream is) throws LSException {
        return null;
    }

    @Override
    public void save(Library library, ZipOutputStream os) throws LSException {
    }

    @Override
    public Library load(ZipInputStream is) throws LSException {
        return null;
    }
}
