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

    private static final Logger log = LoggerFactory.getLogger(ScriptActionImpl.class);

    private final String location;
    private final ScriptDescription desc;

    private Script _cachedScript;

    public ScriptActionImpl(String location, ScriptDescription desc) {
        this.location = location;
        this.desc = desc;
    }

    @Override
    public String getId() {
        return desc.getId();
    }

    @Override
    public String getLocalizedName() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(location + ".names");
            return bundle.getString(getId());
        } catch (Exception e) {
            // default name in case non-existence of resource for language
            return getName();
        }
    }

    @Override
    public String getName() {
        return desc.getName();
    }

    @Override
    public Script getScript() {
        if (_cachedScript == null) {
            String sLoc = location + "/" + desc.getLocation();
            String src = ScriptsLoader.loadFile(getClass().getClassLoader().getResourceAsStream(sLoc));
            try {
                _cachedScript = Script.createScript(src, desc.getLanguage());
            } catch (GrafikonException e) {
                log.error("Couldn't create script.", e);
            }
        }
        return _cachedScript;
    }

    @Override
    public void execute(TrainDiagram diagram) throws GrafikonException {
        getScript().evaluateWithException(getBinding(diagram, null));
    }

    @Override
    public void execute(TrainDiagram diagram, Map<String, Object> binding) throws GrafikonException {
        Map<String, Object> b = new HashMap<>(getBinding(diagram, binding));
        getScript().evaluateWithException(b);
    }

    private Map<String, Object> getBinding(TrainDiagram diagram, Map<String, Object> params) {
        HashMap<String, Object> binding = new HashMap<>();
        if (params != null) {
            binding.putAll(params);
        }
        binding.put("diagram", diagram);
        return  binding;
    }
}
