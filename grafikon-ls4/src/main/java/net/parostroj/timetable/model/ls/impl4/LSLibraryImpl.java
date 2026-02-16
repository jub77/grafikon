package net.parostroj.timetable.model.ls.impl4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import net.parostroj.timetable.model.Permissions;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.ls.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryBuilder;
import net.parostroj.timetable.model.library.LibraryItem;
import net.parostroj.timetable.model.library.LibraryItemType;

public class LSLibraryImpl extends AbstractLSImpl implements LSLibrary {

    private static final Logger log = LoggerFactory.getLogger(LSLibraryImpl.class);

    private static final ModelVersion CURRENT_VERSION;
    private static final List<ModelVersion> VERSIONS;

    static {
        VERSIONS = getVersions("1.0", "1.1", "1.1.1", "1.2");
        CURRENT_VERSION = getLatestVersion(VERSIONS);
    }

    private final LSSerializer lss;

    public LSLibraryImpl() throws LSException {
        lss = new LSSerializer(true);
    }

    @Override
    public List<ModelVersion> getLoadVersions() {
        return VERSIONS;
    }

    @Override
    public ModelVersion getSaveVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public void saveItem(LibraryItem item, OutputStream os) throws LSException {
        lss.save(os, new LSLibraryItem(item));
    }

    @Override
    public LibraryItem loadItem(InputStream is, LSFeature... features) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Library library, LSSink sink) throws LSException {
        try (sink) {
            // save metadata
            this.createMetadata(METADATA_KEY_LIBRARY_VERSION).store(sink.nextItem(METADATA), null);
            for (LibraryItemType itemType : LibraryItemType.values()) {
                for (LibraryItem item : library.getItems().get(itemType)) {
                    this.save(sink, String.format("%s/%s.%s",
                            LSLibraryTypeMapping.typeToDirectory(item.getType()),
                            item.getObject().getId(),
                            "xml"), new LSLibraryItem(item));
                }
            }
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    private void save(LSSink sink, String itemName, Object saved) throws LSException, IOException {
        lss.save(sink.nextItem(itemName), saved);
    }

    @Override
    public Library load(LSSource source, LSFeature... features) throws LSException {
        try {
            LSSource.Item item;
            ModelVersion version = (ModelVersion) properties.get(VERSION_PROPERTY);
            LibraryBuilder libraryBuilder = new LibraryBuilder(
                    LibraryBuilder.newConfig().setPermissions(getPermissions(features)));
            while ((item = source.nextItem()) != null) {
                if (item.name().equals(METADATA)) {
                    // check major and minor version (do not allow load newer versions)
                    Properties props = new Properties();
                    props.load(item.stream());
                    version = checkVersion(METADATA_KEY_LIBRARY_VERSION, props);
                    continue;
                }
                LSLibraryItem lsItem = lss.load(item.stream(), LSLibraryItem.class);
                lsItem.createLibraryItem(libraryBuilder);
            }
            log.debug("Loaded version: {}", version != null ? version : "<missing>");
            return libraryBuilder.build();
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    private Permissions getPermissions(LSFeature[] features) {
        return Stream.of(features)
                .filter(feature -> LSFeature.RAW_DIAGRAM == feature)
                .findAny()
                .map(f -> Permissions.forType(TrainDiagramType.RAW))
                .orElseGet(() -> Permissions.forType(TrainDiagramType.NORMAL));
    }
}
