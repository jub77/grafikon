package net.parostroj.timetable.actions.scripts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.parostroj.timetable.model.Script;

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
    
    public static synchronized List<ScriptDescription> getPredefinedScripts() {
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

    private static ScriptDescription getById(String id) {
        for (ScriptDescription desc : getPredefinedScripts()) {
            if (desc.getId().equals(id))
                return desc;
        }
        return null;
    }
    
    static String loadFile(InputStream is) {
        StringBuilder b = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                b.append(line).append('\n');
            }
        } catch (Exception e) {
            LOG.error("Error reading file.", e);
        }
        return b.toString();
    }
}
