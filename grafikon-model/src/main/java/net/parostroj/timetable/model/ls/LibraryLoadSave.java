package net.parostroj.timetable.model.ls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Interface for loading/saving library items.
 *
 * @author jub
 */
public interface LibraryLoadSave {

    void saveItem(Object item, OutputStream os) throws LSException;

    Object loadItem(InputStream is) throws LSException;

    void saveItems(Iterable<? extends Object> items, ZipOutputStream os) throws LSException;

    Collection<Object> loadItems(ZipInputStream is) throws LSException;

    Set<Class<?>> getSupportedItems();
}
