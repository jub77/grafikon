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

public class LSLibraryImpl implements LSLibrary {

    @Override
    public List<ModelVersion> getLoadVersions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModelVersion getSaveVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveItem(LibraryItem item, OutputStream os) throws LSException {
        // TODO Auto-generated method stub

    }

    @Override
    public LibraryItem loadItem(InputStream is) throws LSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save(Library library, ZipOutputStream os) throws LSException {
        // TODO Auto-generated method stub

    }

    @Override
    public Library load(ZipInputStream is) throws LSException {
        // TODO Auto-generated method stub
        return null;
    }

}
