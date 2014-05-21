package net.parostroj.timetable.actions.scripts;

import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Action that executes script over diagram.
 *
 * @author jub
 */
public class ScriptAction {

    private final String id;
    private final String name;
    private final String localizedName;
    private final Script script;
    private Map<String, Object> binding;

    public ScriptAction(String id, String name, String localizedName, Script script) {
        this.id = id;
        this.name = name;
        this.script = script;
        this.localizedName = localizedName;
    }

    public ScriptAction(Script script) {
        this(null, null, null, script);
    }

    public String getId() {
        return id;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getName() {
        return name;
    }

    public Script getScript() {
        return script;
    }

    public void execute(TrainDiagram diagram) throws GrafikonException {
        script.evaluateWithException(getBinding(diagram));
    }

    public void execute(TrainDiagram diagram, Map<String, Object> binding) throws GrafikonException {
        Map<String, Object> b = new HashMap<String, Object>(getBinding(diagram));
        b.putAll(binding);
        script.evaluateWithException(b);
    }

    private Map<String, Object> getBinding(TrainDiagram diagram) {
        if (binding == null) {
            binding = new HashMap<String, Object>();
        }
        binding.put("diagram", diagram);
        return  binding;
    }
}
