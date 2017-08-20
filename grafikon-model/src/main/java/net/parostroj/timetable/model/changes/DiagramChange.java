package net.parostroj.timetable.model.changes;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diagram change (extracted from events).
 *
 * @author jub
 */
public class DiagramChange {

    public static enum Type {
        DIAGRAM("diagram"), NET("net"), TRAIN("train"), TRAIN_TYPE("train_type"),
        NODE("node"), LINE("line"), TEXT_ITEM("text_item"), IMAGE("image"),
        TRAINS_CYCLE("trains_cycle"), ENGINE_CLASS("engine_class"),
        LINE_CLASS("line_class"), ROUTE("route"), OUTPUT_TEMPLATE("output_template"), OUTPUT("output"),
        CYCLE_TYPE("cycle_type"), GROUP("group"), FREIGHT_NET("freight_net"), REGION("region"),
        COMPANY("company"), TRAIN_TYPE_CATEGORY("train_type_category");

        private String key;

        private Type(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return getString(key);
        }
    }

    public static enum Action {
        ADDED("added"), REMOVED("removed"), MODIFIED("modified"), MOVED("moved");

        private String key;

        private Action(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return getString(key);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(DiagramChange.class);

    private Type type;
    private Action action;
    private String objectId;
    private String object;
    private List<DiagramChangeDescription> descriptions;

    public DiagramChange() {}

    public DiagramChange(Type type, String objectId) {
        this.type = type;
        this.objectId = objectId;
    }

    public DiagramChange(Type type, Action action, String objectId) {
        this.type = type;
        this.objectId = objectId;
        this.action = action;
    }

    public String getObjectId() {
        return objectId;
    }

    public Type getType() {
        return type;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<DiagramChangeDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DiagramChangeDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public void addDescription(DiagramChangeDescription description) {
        if (descriptions == null)
            descriptions = new LinkedList<>();
        descriptions.add(description);
    }

    public void addDescriptionUnique(DiagramChangeDescription description) {
        boolean add = true;
        if (descriptions != null)
            add = !descriptions.contains(description);
        if (add)
            this.addDescription(description);
    }

    @Override
    public String toString() {
        return String.format("Change(%s,%s,%s,%s)", type.toString(), objectId, action != null ? action.toString() : "<null>", object);
    }

    static String getString(String key) {
        try {
            return getStringWithException(key);
        } catch (MissingResourceException e) {
            log.warn("Error getting text for key: {}", key);
            return "MISSING STRING FOR KEY: " + key;
        }
    }

    static String getStringWithException(String key) {
        return ResourceBundle.getBundle("net.parostroj.timetable.model.changes.diagram_change_texts").getString(key);
    }

    static String getStringWithoutException(String key) {
        String result = null;
        try {
            result = getStringWithException(key);
        } catch (MissingResourceException e) {
            log.warn("Key not found: {}", e.getKey());
            result = key;
        }
        return result;
    }
}
