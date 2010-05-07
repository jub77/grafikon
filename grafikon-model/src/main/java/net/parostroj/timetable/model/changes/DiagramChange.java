package net.parostroj.timetable.model.changes;

import java.util.Set;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        LINE_CLASS("line_class"), ROUTE("route");

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
        ADDED("added"), REMOVED("removed"), MODIFIED("modified");

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

    private static final Logger LOG = Logger.getLogger(DiagramChange.class.getName());

    private Type type;
    private Action action;
    private String objectId;
    private String object;
    private String description;
    private String[] params;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescription(String description, String... params) {
        this.description = description;
        this.params = params;
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

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String getFormattedDescription() {
        return String.format(getString(description), (Object[])params);
    }

    @Override
    public String toString() {
        return String.format("Change(%s,%s,%s,%s,%s)", type.toString(), objectId, action != null ? action.toString() : "<null>", object, description);
    }

    static String getString(String key) {
        try {
            return ResourceBundle.getBundle("net.parostroj.timetable.model.changes.diagram_change_texts").getString(key);
        } catch (MissingResourceException e) {
            LOG.log(Level.WARNING, "Error getting text for key: " + key, e);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
