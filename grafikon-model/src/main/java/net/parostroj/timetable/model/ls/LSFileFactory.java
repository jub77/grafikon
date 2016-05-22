package net.parostroj.timetable.model.ls;

/**
 * Factory for loading/saving train diagram.
 *
 * @author jub
 */
public class LSFileFactory extends AbstractLSFactory<LSFile> {

    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final LSFileFactory instance = new LSFileFactory();
    private static boolean initialized = false;

    public LSFileFactory() {
        super(LSFile.class, METADATA_KEY_MODEL_VERSION);
    }

    public static synchronized LSFileFactory getInstance() {
        if (!initialized) {
            instance.init();
            initialized = true;
        }
        return instance;
    }
}
