package net.parostroj.timetable.actions.scripts;

import java.io.File;
import java.util.ResourceBundle;

import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.Script.Language;

/**
 * Description of script.
 *  
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "description", "language", "location" })
public class ScriptDescription {
    
    private static final Logger LOG = LoggerFactory.getLogger(ScriptDescription.class);

    private String id;
    private String name;
    private String description;
    private Language language;
    private String location;
    
    private Script _cachedScript;
    private ScriptAction _cachedScriptAction;

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

    public String getDescription() {
        return description;
    }

    public String getLocalizedName() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("scripts.names");
            return bundle.getString(getId());
        } catch (Exception e) {
            LOG.error("Error getting name.", e);
            return getName();
        }
    }

    public void setDescription(String description) {
        this.description = description;
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
    
    public Script getScript() {
        if (_cachedScript == null) {
            String sLoc = PredefinedScriptsLoader.SCRIPTS_LOCATION + "/" + getLocation();
            String src = PredefinedScriptsLoader.loadFile(getClass().getClassLoader().getResourceAsStream(sLoc));
            try {
                _cachedScript = Script.createScript(src, getLanguage());
            } catch (GrafikonException e) {
                LOG.error("Couldn't create script.", e);
            }
        }
        return _cachedScript;
    }
    
    public ScriptAction getScriptAction() {
        if (_cachedScriptAction == null) {
            _cachedScriptAction = new ScriptAction(getName(), getDescription(), getScript());
        }
        return _cachedScriptAction;
    }
}
