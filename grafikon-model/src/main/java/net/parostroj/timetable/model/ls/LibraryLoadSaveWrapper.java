package net.parostroj.timetable.model.ls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Wrapper that allows only load operation to be called.
 *
 * @author jub
 */
public class LibraryLoadSaveWrapper implements LibraryLoadSave {

    private LibraryLoadSave impl;

    public LibraryLoadSaveWrapper(LibraryLoadSave impl) {
        this.impl = impl;
    }

    @Override
    public void saveItem(Object item, OutputStream os) throws LSException {
        throw new LSException("Save operation not supported.");
    }

    @Override
    public Object loadItem(InputStream is) throws LSException {
        return impl.loadItem(is);
    }

    @Override
    public void saveItems(Iterable<? extends Object> items, ZipOutputStream os) throws LSException {
        throw new LSException("Save operation not supported.");
    }

    @Override
    public Collection<Object> loadItems(ZipInputStream is) throws LSException {
        return impl.loadItems(is);
    }

    @Override
    public Set<Class<?>> getSupportedItems() {
        return impl.getSupportedItems();
    }
}
