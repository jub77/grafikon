package net.parostroj.timetable.actions.scripts;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Script.Language;

/**
 * Description of script.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "language", "location" })
class ScriptDescription {

    private String id;
    private String name;
    private Language language;
    private String location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
