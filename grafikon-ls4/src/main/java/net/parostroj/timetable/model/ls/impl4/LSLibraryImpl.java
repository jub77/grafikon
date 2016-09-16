package net.parostroj.timetable.model.ls.impl4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryBuilder;
import net.parostroj.timetable.model.library.LibraryItem;
import net.parostroj.timetable.model.library.LibraryItemType;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSLibrary;
import net.parostroj.timetable.model.ls.ModelVersion;

public class LSLibraryImpl extends AbstractLSImpl implements LSLibrary {

    private static final Logger log = LoggerFactory.getLogger(LSLibraryImpl.class);

    private static final ModelVersion CURRENT_VERSION;
    private static final List<ModelVersion> VERSIONS;

    static {
        VERSIONS = getVersions("1.0");
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
    public LibraryItem loadItem(InputStream is) throws LSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Library library, ZipOutputStream zipOutput) throws LSException {
        try {
            // save metadata
            zipOutput.putNextEntry(new ZipEntry(METADATA));
            this.createMetadata(METADATA_KEY_LIBRARY_VERSION).store(zipOutput, null);
            for (LibraryItemType itemType : LibraryItemType.values()) {
                for (LibraryItem item : library.getItems().get(itemType)) {
                    this.save(zipOutput, String.format("%s/%s.%s",
                            LSLibraryTypeMapping.typeToDirectory(item.getType()),
                            item.getObject().getId(),
                            "xml"), new LSLibraryItem(item));
                }
            }
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    private void save(ZipOutputStream zipOutput, String zipEntryName, Object saved) throws LSException, IOException {
        zipOutput.putNextEntry(new ZipEntry(zipEntryName));
        lss.save(zipOutput, saved);
    }

    @Override
    public Library load(ZipInputStream is) throws LSException {
        try {
            ZipEntry entry = null;
            ModelVersion version = (ModelVersion) properties.get(VERSION_PROPERTY);
            LibraryBuilder libraryBuilder = new LibraryBuilder();
            while ((entry = is.getNextEntry()) != null) {
                if (entry.getName().equals(METADATA)) {
                    // check major and minor version (do not allow load newer versions)
                    Properties props = new Properties();
                    props.load(is);
                    version = checkVersion(METADATA_KEY_LIBRARY_VERSION, props);
                    continue;
                }
                LSLibraryItem lsItem = lss.load(is, LSLibraryItem.class);
                lsItem.createLibraryItem(libraryBuilder);
            }
            log.debug("Loaded version: {}", version != null ? version : "<missing>");
            return libraryBuilder.build();
        } catch (IOException e) {
            throw new LSException(e);
        }
    }
}
