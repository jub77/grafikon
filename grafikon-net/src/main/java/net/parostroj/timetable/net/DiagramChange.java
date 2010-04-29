package net.parostroj.timetable.net;

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

    public static enum DiagramChangeType {
        DIAGRAM("diagram"), NET("net"), TRAIN("train"), TRAIN_TYPE("train_type"),
        NODE("node"), LINE("line"), TEXT_ITEM("text_item"), TRAINS_CYCLE("trains_cycle");

        private String key;

        private DiagramChangeType(String key) {
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

    private DiagramChangeType type;
    private String objectId;
    private String description;
    private String object;

    public DiagramChange() {}

    public DiagramChange(DiagramChangeType type, String objectId) {
        this.type = type;
        this.objectId = objectId;
    }

    public String getDescription() {
        return description;
    }

    public String getObject() {
        return object;
    }

    public void setObjectKey(String object) {
        this.object = getString(object);
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setDescriptionKey(String description) {
        this.description = getString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectId() {
        return objectId;
    }

    public DiagramChangeType getType() {
        return type;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setType(DiagramChangeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("Change(%s,%s,%s,%s)", type.toString(), objectId, object, description);
    }

    private static String getString(String key) {
        try {
            return ResourceBundle.getBundle("net.parostroj.timetable.net.diagram_change_texts").getString(key);
        } catch (MissingResourceException e) {
            LOG.log(Level.WARNING, "Error getting text for key: " + key, e);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
