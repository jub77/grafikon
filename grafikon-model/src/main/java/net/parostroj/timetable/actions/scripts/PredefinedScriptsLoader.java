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

    static final String LIST_LOCATION = "scripts/list.xml";
    static final String SCRIPTS_LOCATION = "scripts";

    private static List<ScriptDescription> list;

    public static synchronized List<ScriptDescription> getScripts() {
        if (list == null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ScriptList.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                InputStream stream = PredefinedScriptsLoader.class.getClassLoader().getResourceAsStream(LIST_LOCATION);
                ScriptList scriptList = (ScriptList) unmarshaller.unmarshal(stream);
                list = scriptList.getScripts();
            } catch (JAXBException e) {
                LOG.error("Error loading list of scripts.", e);
            }
            if (list == null)
                list = Collections.emptyList();
        }
        return list;
    }

    public static Script getScript(String id) {
        ScriptDescription desc = getById(id);
        return desc == null ? null : desc.getScript();
    }

    public static ScriptAction getScriptAction(String id) {
        ScriptDescription desc = getById(id);
        return desc == null ? null : desc.getScriptAction();
    }

    public Collection<String> getScriptIds() {
        List<ScriptDescription> scripts = getScripts();
        List<String> result = new ArrayList<String>(scripts.size());
        for (ScriptDescription s : scripts) {
            result.add(s.getId());
        }
        return result;
    }

    private static ScriptDescription getById(String id) {
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
            LOG.error("Error reading file.", e);
            return "";
        }
    }
}
