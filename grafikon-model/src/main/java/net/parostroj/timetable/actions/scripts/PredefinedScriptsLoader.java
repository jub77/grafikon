package net.parostroj.timetable.actions.scripts;

import java.io.File;
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

    private static final Map<String, PredefinedScriptsLoader> loaders = new HashMap<String, PredefinedScriptsLoader>();

    private final String location;
    private List<ScriptDescription> list;

    private PredefinedScriptsLoader(String location) {
        this.location = location;
    }

    public synchronized static PredefinedScriptsLoader getScriptsLoader(String location) {
        PredefinedScriptsLoader loader = loaders.get(location);
        if (loader == null) {
            loader = new PredefinedScriptsLoader(location);
            loaders.put(location, loader);
        }
        return loader;
    }

    public static PredefinedScriptsLoader getDefaultScriptsLoader() {
        return getScriptsLoader(DEFAULT_SCRIPTS_LOCATION);
    }

    public synchronized List<ScriptDescription> getScripts() {
        if (list == null) {
            try {
                JAXBContext context = JAXBContext.newInstance(ScriptList.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                InputStream stream = PredefinedScriptsLoader.class.getClassLoader().getResourceAsStream(location + File.separator + LIST);
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

    public Script getScript(String id) {
        ScriptDescription desc = getById(id);
        return desc == null ? null : desc.getScript(this);
    }

    public ScriptAction getScriptAction(String id) {
        ScriptDescription desc = getById(id);
        return desc == null ? null : desc.getScriptAction(this);
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
            LOG.error("Error reading file.", e);
            return "";
        }
    }

    public String getLocation() {
        return location;
    }
}
