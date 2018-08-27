package net.parostroj.timetable.model.ls;

/**
 * Factory for loading/saving train diagram.
 *
 * @author jub
 */
public class LSFileFactory extends AbstractLSFactory<LSFile> {

    private static LSFileFactory instance;

    public LSFileFactory() {
        super(LSFile.class, LSFile.METADATA_KEY_MODEL_VERSION, LSFile.METADATA, file -> new LSFileWrapper(file));
    }

    public static synchronized LSFileFactory getInstance() {
        if (instance == null) {
            instance = new LSFileFactory();
        }
        return instance;
    }
}
