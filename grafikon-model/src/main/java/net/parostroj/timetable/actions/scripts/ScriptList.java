package net.parostroj.timetable.actions.scripts;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * List of scripts.
 *
 * @author jub
 */
@XmlRootElement(name = "scripts")
class ScriptList {

    private List<ScriptDescription> scripts;

    @XmlElement(name = "script")
    public List<ScriptDescription> getScripts() {
        return scripts;
    }

    public void setScripts(List<ScriptDescription> scripts) {
        this.scripts = scripts;
    }
}
