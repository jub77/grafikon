package net.parostroj.timetable.actions.scripts;

import net.parostroj.timetable.loader.DataItem;
import net.parostroj.timetable.loader.DataItemList;
import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.ls.LSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Returns list of predefined scripts.
 *
 * @author jub
 */
public class ScriptsLoader {

    private static final Logger log = LoggerFactory.getLogger(ScriptsLoader.class);

    private static final String DEFAULT_SCRIPTS_LOCATION = "/scripts";
    private static final String LIST = "list.yaml";

    private final String location;
    private Map<String, ScriptAction> scriptActions;

    private ScriptsLoader(String location) {
        this.location = location;
    }

    public static ScriptsLoader newScriptsLoader(String location) {
        return new ScriptsLoader(location);
    }

    public static ScriptsLoader newDefaultScriptsLoader() {
        return newScriptsLoader(DEFAULT_SCRIPTS_LOCATION);
    }

    public Map<String, ScriptAction> getScriptActionsMap() {
        if (scriptActions == null) {
            try {
                DataItemLoader<Script> loader = DataItemLoader.getFromResources(location, LIST, (is, dataItem) -> {
                    try {
                        String src = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        return Script.create(src, Script.Language.GROOVY);
                    } catch (IOException e) {
                        throw new LSException("Error reading action: " + e.getMessage(), e);
                    }
                });
                DataItemList list = loader.loadList();
                scriptActions = new LinkedHashMap<>(list.items().size());
                for (DataItem item : list.items()) {
                    scriptActions.put(item.id(), new ScriptActionImpl(loader, item));
                }
                scriptActions = Collections.unmodifiableMap(scriptActions);
            } catch (LSException e) {
                log.error("Error loading list of scripts.", e);
            }
            if (scriptActions == null)
                scriptActions = Map.of();
        }
        return scriptActions;
    }
}
