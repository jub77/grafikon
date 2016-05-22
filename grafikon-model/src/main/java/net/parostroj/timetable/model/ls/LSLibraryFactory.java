package net.parostroj.timetable.model.ls;

/**
 * Factory for loading/saving train diagram.
 *
 * @author jub
 */
public class LSLibraryFactory extends AbstractLSFactory<LSLibrary> {

    private static final String METADATA_KEY_LIBRARY_VERSION = "library.version";
    private static final LSLibraryFactory instance = new LSLibraryFactory();
    private static boolean initialized = false;

    public LSLibraryFactory() {
        super(LSLibrary.class, METADATA_KEY_LIBRARY_VERSION);
    }

    public static synchronized LSLibraryFactory getInstance() {
        if (!initialized) {
            instance.init();
            initialized = true;
        }
        return instance;
    }
}
