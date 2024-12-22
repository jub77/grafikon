package net.parostroj.timetable.actions.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
    private static final String LIST = "list.yaml";

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
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                InputStream stream = ScriptsLoader.class.getClassLoader().getResourceAsStream(location + "/" + LIST);
                ScriptList scriptList = mapper.readValue(stream, ScriptList.class);
                List<ScriptDescription> sList = scriptList.scripts();
                scriptActions = new LinkedHashMap<>(sList.size());
                for (ScriptDescription d : sList) {
                    scriptActions.put(d.id(), new ScriptActionImpl(location, d));
                }
                keyView = Collections.unmodifiableCollection(scriptActions.keySet());
                actionsView = Collections.unmodifiableCollection(scriptActions.values());
            } catch (IOException e) {
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
