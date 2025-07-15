package net.parostroj.timetable.model.ls;

import java.io.InputStream;
import java.io.OutputStream;

import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItem;

/**
 * Interface for loading/saving library items.
 *
 * @author jub
 */
public interface LSLibrary extends LSVersions, LSConfigurable, LS<Library> {

    String METADATA_KEY_LIBRARY_VERSION = "library.version";
    String METADATA = "metadata.properties";

    void saveItem(LibraryItem item, OutputStream os) throws LSException;

    LibraryItem loadItem(InputStream is, LSFeature... features) throws LSException;
}
