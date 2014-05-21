package net.parostroj.timetable.actions.scripts;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Action that executes script over diagram.
 *
 * @author jub
 */
class ScriptActionImpl implements ScriptAction {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptActionImpl.class);

    private final String location;
    private final ScriptDescription desc;
    private Map<String, Object> binding;

    private Script _cachedScript;

    public ScriptActionImpl(String location, ScriptDescription desc) {
        this.location = location;
        this.desc = desc;
    }

    @Override
    public String getId() {
        return desc.getId();
    }

    public String getLocalizedName() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(location + ".names");
            return bundle.getString(getId());
        } catch (Exception e) {
            return getName();
        }
    }

    @Override
    public String getName() {
        return desc.getName();
    }

    public Script getScript() {
        if (_cachedScript == null) {
            String sLoc = location + "/" + desc.getLocation();
            String src = PredefinedScriptsLoader.loadFile(getClass().getClassLoader().getResourceAsStream(sLoc));
            try {
                _cachedScript = Script.createScript(src, desc.getLanguage());
            } catch (GrafikonException e) {
                LOG.error("Couldn't create script.", e);
            }
        }
        return _cachedScript;
    }

    @Override
    public void execute(TrainDiagram diagram) throws GrafikonException {
        getScript().evaluateWithException(getBinding(diagram));
    }

    @Override
    public void execute(TrainDiagram diagram, Map<String, Object> binding) throws GrafikonException {
        Map<String, Object> b = new HashMap<String, Object>(getBinding(diagram));
        b.putAll(binding);
        getScript().evaluateWithException(b);
    }

    private Map<String, Object> getBinding(TrainDiagram diagram) {
        if (binding == null) {
            binding = new HashMap<String, Object>();
        }
        binding.put("diagram", diagram);
        return  binding;
    }
}
