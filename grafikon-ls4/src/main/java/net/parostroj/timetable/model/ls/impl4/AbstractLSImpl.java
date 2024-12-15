package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.parostroj.timetable.model.ls.LSConfigurable;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSVersions;
import net.parostroj.timetable.model.ls.ModelVersion;

public abstract class AbstractLSImpl implements LSConfigurable, LSVersions {

    protected final Map<String, Object> properties;

    public AbstractLSImpl() {
        properties = new HashMap<>();
    }

    @Override
    public Object getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    protected ModelVersion checkVersion(String versionKey, Properties props) throws LSException {
        ModelVersion current = getSaveVersion();
        ModelVersion loaded = ModelVersion.parseModelVersion(props.getProperty(versionKey));
        if (current.compareTo(loaded) < 0) {
            throw new LSException(String.format("Current version [%s] is older than the version of loaded file [%s].", current, loaded));
        }
        return loaded;
    }

    protected static List<ModelVersion> getVersions(String... versions) {
        List<ModelVersion> versionList = new ArrayList<>(versions.length);
        for (String version : versions) {
            versionList.add(ModelVersion.parseModelVersion(version));
        }
        return Collections.unmodifiableList(versionList);
    }

    protected static ModelVersion getLatestVersion(List<ModelVersion> versions) {
        return versions.getLast();
    }

    protected Properties createMetadata(String versionKey) {
        Properties metadata = new Properties();
        metadata.setProperty(versionKey, getSaveVersion().toString());
        return metadata;
    }
}
