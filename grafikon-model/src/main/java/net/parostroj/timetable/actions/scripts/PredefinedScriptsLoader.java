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

    private static final Logger LOG = LoggerFactory.getLogger(PredefinedScriptsLoader.class);

    private static final String DEFAULT_SCRIPTS_LOCATION = "scripts";
    private static final String LIST = "list.xml";

    private final String location;
    private Map<String, ScriptAction> scriptActions;

    private Collection<ScriptAction> _actionsView;
    private Collection<String> _keyView;

    private PredefinedScriptsLoader(String location) {
        this.location = location;
    }

    public static PredefinedScriptsLoader newScriptsLoader(String location) {
        return new PredefinedScriptsLoader(location);
    }

    public static PredefinedScriptsLoader newDefaultScriptsLoader() {
        return newScriptsLoader(DEFAULT_SCRIPTS_LOCATION);
    }

    synchronized Map<String, ScriptAction> getScriptActionsMap() {
        if (scriptActions == null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ScriptList.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                InputStream stream = PredefinedScriptsLoader.class.getClassLoader().getResourceAsStream(location + "/" + LIST);
                ScriptList scriptList = (ScriptList) unmarshaller.unmarshal(stream);
                List<ScriptDescription> sList = scriptList.getScripts();
                scriptActions = new LinkedHashMap<String, ScriptAction>(sList.size());
                for (ScriptDescription d : sList) {
                    scriptActions.put(d.getId(), new ScriptActionImpl(location, d));
                }
                _keyView = Collections.unmodifiableCollection(scriptActions.keySet());
                _actionsView = Collections.unmodifiableCollection(scriptActions.values());
            } catch (JAXBException e) {
                LOG.error("Error loading list of scripts.", e);
            }
            if (scriptActions == null)
                scriptActions = Collections.emptyMap();
        }
        return scriptActions;
    }

    public Collection<ScriptAction> getScriptActions() {
        getScriptActionsMap();
        return _actionsView;
    }

    public Collection<String> getScriptIds() {
        getScriptActionsMap();
        return _keyView;
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
            LOG.error("Error reading file.", e);
            return "";
        }
    }

    String getLocation() {
        return location;
    }
}
