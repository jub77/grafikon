package net.parostroj.timetable.actions.scripts;

import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.loader.DataItem;
import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.model.TranslatedString;
import net.parostroj.timetable.model.ls.LSException;
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

    private final DataItemLoader<Script> loader;
    private final DataItem item;
    private Script cachedScript;

    public ScriptActionImpl(DataItemLoader<Script> loader, DataItem item) {
        this.loader = loader;
        this.item = item;
    }

    @Override
    public String getId() {
        return item.id();
    }

    @Override
    public TranslatedString getName() {
        return item.name();
    }

    private Script getScript() {
        if (cachedScript == null) {
            try {
                cachedScript = loader.loadItem(item);
            } catch (GrafikonException | LSException e) {
                log.error("Couldn't create script.", e);
            }
        }
        return cachedScript;
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
