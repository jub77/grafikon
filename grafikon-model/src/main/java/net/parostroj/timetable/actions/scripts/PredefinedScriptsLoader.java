package net.parostroj.timetable.actions.scripts;

import java.io.InputStream;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.utils.Conversions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns list of predefined scripts.
 *
 * @author jub
 */
public class PredefinedScriptsLoader {

    private static final Logger log = LoggerFactory.getLogger(PredefinedScriptsLoader.class);

    private static final String DEFAULT_SCRIPTS_LOCATION = "scripts";
    private static final String LIST = "list.xml";

    private final String location;
    private List<ScriptDescription> list;

    private PredefinedScriptsLoader(String location) {
        this.location = location;
    }

    public static PredefinedScriptsLoader newScriptsLoader(String location) {
        return new PredefinedScriptsLoader(location);
    }

    public static PredefinedScriptsLoader newDefaultScriptsLoader() {
        return newScriptsLoader(DEFAULT_SCRIPTS_LOCATION);
    }

    synchronized List<ScriptDescription> getScripts() {
        if (list == null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ScriptList.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                InputStream stream = PredefinedScriptsLoader.class.getClassLoader().getResourceAsStream(location + "/" + LIST);
                ScriptList scriptList = (ScriptList) unmarshaller.unmarshal(stream);
                list = scriptList.getScripts();
            } catch (JAXBException e) {
                log.error("Error loading list of scripts.", e);
            }
            if (list == null)
                list = Collections.emptyList();
        }
        return list;
    }

    public Script getScript(String id) {
        ScriptDescription desc = getById(id);
        return desc == null ? null : desc.getScript(this);
    }

    public ScriptAction getScriptAction(String id) {
        ScriptDescription desc = getById(id);
        return desc == null ? null : desc.getScriptAction(this);
    }

    public List<ScriptAction> getScriptActions() {
        List<ScriptDescription> scripts = getScripts();
        List<ScriptAction> result = new ArrayList<ScriptAction>(scripts.size());
        for (ScriptDescription script : scripts) {
            result.add(script.getScriptAction(this));
        }
        return result;
    }

    public Collection<String> getScriptIds() {
        List<ScriptDescription> scripts = getScripts();
        List<String> result = new ArrayList<String>(scripts.size());
        for (ScriptDescription s : scripts) {
            result.add(s.getId());
        }
        return result;
    }

    private ScriptDescription getById(String id) {
        for (ScriptDescription desc : getScripts()) {
            if (desc.getId().equals(id))
                return desc;
        }
        return null;
    }

    static String loadFile(InputStream is) {
        try {
            return Conversions.loadFile(is);
        } catch (Exception e) {
            log.error("Error reading file.", e);
            return "";
        }
    }

    String getLocation() {
        return location;
    }
}
