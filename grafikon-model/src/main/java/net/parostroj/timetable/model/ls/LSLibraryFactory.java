package net.parostroj.timetable.model.ls;

/**
 * Factory for loading/saving train diagram.
 *
 * @author jub
 */
public class LSLibraryFactory extends AbstractLSFactory<LSLibrary> {

    private static LSLibraryFactory instance = new LSLibraryFactory();

    public LSLibraryFactory() {
        super(LSLibrary.class, LSLibrary.METADATA_KEY_LIBRARY_VERSION, LSLibrary.METADATA);
    }

    public static synchronized LSLibraryFactory getInstance() {
        if (instance == null) {
            instance = new LSLibraryFactory();
        }
        return instance;
    }
}
