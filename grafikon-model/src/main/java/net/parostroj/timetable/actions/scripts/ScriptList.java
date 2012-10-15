package net.parostroj.timetable.actions.scripts;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
