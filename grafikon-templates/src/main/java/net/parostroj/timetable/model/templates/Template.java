package net.parostroj.timetable.model.templates;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Template information.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "filename", "version", "name", "description"})
public class Template {

    private String id;
    private String filename;
    private ModelVersion version;
    private LocalizedString name;
    private LocalizedString description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @XmlJavaTypeAdapter(type = String.class, value = LModelVersionAdapter.class)
    public ModelVersion getVersion() {
        return version;
    }

    public void setVersion(ModelVersion version) {
        this.version = version;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s(%s,%s)", id, filename != null ? filename : "-", version != null ? version : "-");
    }
}
