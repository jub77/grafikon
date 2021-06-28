package net.parostroj.timetable.actions.scripts;

import java.io.InputStream;
import java.util.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.utils.Conversions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns list of predefined scripts.
 *
 * @author jub
 */
public class ScriptsLoader {

    private static final Logger log = LoggerFactory.getLogger(ScriptsLoader.class);

    private static final String DEFAULT_SCRIPTS_LOCATION = "scripts";
    private static final String LIST = "list.xml";

    private final String location;
    private Map<String, ScriptAction> scriptActions;

    private Collection<ScriptAction> actionsView;
    private Collection<String> keyView;

    private ScriptsLoader(String location) {
        this.location = location;
    }

    public static ScriptsLoader newScriptsLoader(String location) {
        return new ScriptsLoader(location);
    }

    public static ScriptsLoader newDefaultScriptsLoader() {
        return newScriptsLoader(DEFAULT_SCRIPTS_LOCATION);
    }

    synchronized Map<String, ScriptAction> getScriptActionsMap() {
        if (scriptActions == null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ScriptList.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                InputStream stream = ScriptsLoader.class.getClassLoader().getResourceAsStream(location + "/" + LIST);
                ScriptList scriptList = (ScriptList) unmarshaller.unmarshal(stream);
                List<ScriptDescription> sList = scriptList.getScripts();
                scriptActions = new LinkedHashMap<>(sList.size());
                for (ScriptDescription d : sList) {
                    scriptActions.put(d.getId(), new ScriptActionImpl(location, d));
                }
                keyView = Collections.unmodifiableCollection(scriptActions.keySet());
                actionsView = Collections.unmodifiableCollection(scriptActions.values());
            } catch (JAXBException e) {
                log.error("Error loading list of scripts.", e);
            }
            if (scriptActions == null)
                scriptActions = Collections.emptyMap();
        }
        return scriptActions;
    }

    public Collection<ScriptAction> getScriptActions() {
        getScriptActionsMap();
        return actionsView;
    }

    public Collection<String> getScriptIds() {
        getScriptActionsMap();
        return keyView;
    }

    public Script getScript(String id) {
        ScriptAction action = getById(id);
        return action == null ? null : action.getScript();
    }

    public ScriptAction getScriptAction(String id) {
        return getById(id);
    }

    private ScriptAction getById(String id) {
        return getScriptActionsMap().get(id);
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
