package net.parostroj.timetable.model.ls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItem;

/**
 * Interface for loading/saving library items.
 *
 * @author jub
 */
public interface LSLibrary extends LSVersions, LSConfigurable {

    String METADATA_KEY_LIBRARY_VERSION = "library.version";
    String METADATA = "metadata.properties";

    void saveItem(LibraryItem item, OutputStream os) throws LSException;

    LibraryItem loadItem(InputStream is, LSFeature... features) throws LSException;

    void save(Library library, ZipOutputStream os) throws LSException;

    Library load(ZipInputStream is, LSFeature... features) throws LSException;
}
