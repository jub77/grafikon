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

    private final String name;
    private final String description;
    private final Script script;
    private Map<String, Object> binding; 

    public ScriptAction(String name, String description, Script script) {
        this.name = name;
        this.description = description;
        this.script = script;
    }

    public ScriptAction(Script script) {
        this(null, null, script);
    }

    public String getDescription() {
        return description;
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
    
    private Map<String, Object> getBinding(TrainDiagram diagram) {
        if (binding == null) {
            binding = new HashMap<String, Object>();
        }
        binding.put("diagram", diagram);
        return  binding;
    }
}
