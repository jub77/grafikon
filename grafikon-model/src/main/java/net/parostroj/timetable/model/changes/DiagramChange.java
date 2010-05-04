package net.parostroj.timetable.model.changes;

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
        NODE("node"), LINE("line"), TEXT_ITEM("text_item"), TRAINS_CYCLE("trains_cycle"),
        ENGINE_CLASS("engine_class");

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

    public static enum SubType {
        ADDED("added"), REMOVED("removed"), MODIFIED("modified"), MOVED("moved");

        private String key;

        private SubType(String key) {
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
    private SubType subType;
    private String objectId;
    private String object;
    private String description;
    private Object[] params;

    public DiagramChange() {}

    public DiagramChange(Type type, String objectId) {
        this.type = type;
        this.objectId = objectId;
    }

    public DiagramChange(Type type, SubType subType, String objectId) {
        this.type = type;
        this.objectId = objectId;
        this.subType = subType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescription(String description, Object... params) {
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

    public SubType getSubType() {
        return subType;
    }

    public void setSubType(SubType subType) {
        this.subType = subType;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return String.format("Change(%s,%s,%s,%s,%s)", type.toString(), objectId, subType != null ? subType.toString() : "<null>", object, description);
    }

    static String getString(String key) {
        try {
            return ResourceBundle.getBundle("net.parostroj.timetable.net.diagram_change_texts").getString(key);
        } catch (MissingResourceException e) {
            LOG.log(Level.WARNING, "Error getting text for key: " + key, e);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
