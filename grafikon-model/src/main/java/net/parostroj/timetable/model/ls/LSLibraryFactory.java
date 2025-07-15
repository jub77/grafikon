package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.library.Library;

/**
 * Factory for loading/saving train diagram.
 *
 * @author jub
 */
public class LSLibraryFactory extends AbstractLSFactory<LSLibrary, Library> implements LSFactory<LSLibrary, Library> {

    private static LSLibraryFactory instance = new LSLibraryFactory();

    public LSLibraryFactory() {
        super(LSLibrary.class, LSLibrary.METADATA_KEY_LIBRARY_VERSION, LSLibrary.METADATA, LSLibraryWrapper::new);
    }

    public static synchronized LSLibraryFactory getInstance() {
        if (instance == null) {
            instance = new LSLibraryFactory();
        }
        return instance;
    }
}
