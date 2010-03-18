package net.parostroj.timetable.model.ls.impl3;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Scale;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Pair;

/**
 * One item for LSAttributes.
 * 
 * @author jub
 */
@XmlType(propOrder = {"key", "value", "type"})
public class LSAttributesItem {

    private static final Logger LOG = Logger.getLogger(LSAttributesItem.class.getName());
    private String key;
    private String value;
    private String type;

    /**
     * Default constructor.
     */
    public LSAttributesItem() {
    }

    public LSAttributesItem(String key, Object value) {
        this.key = key;
        if (value instanceof String) {
            this.value = (String) value;
            this.type = "string";
        } else if (value instanceof Boolean) {
            this.value = value.toString();
            this.type = "boolean";
        } else if (value instanceof Integer) {
            this.value = value.toString();
            this.type = "integer";
        } else if (value instanceof Double) {
            this.value = value.toString();
            this.type = "double";
        } else if (value instanceof Scale) {
            this.value = value.toString();
            this.type = "scale";
        } else if (value instanceof ObjectWithId) {
            Pair<String, String> pair = this.convertToId((ObjectWithId) value);
            this.type = pair.first;
            this.value = pair.second;
        } else {
            LOG.warning("Cannot convert value to string: " + key);
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object convertValue(TrainDiagram diagram) {
        if (type == null) {
            return null;
        } else if (type.equals("string")) {
            return value;
        } else if (type.equals("boolean")) {
            return Boolean.valueOf(value);
        } else if (type.equals("integer")) {
            return Integer.valueOf(value);
        } else if (type.equals("double")) {
            return Double.valueOf(value);
        } else if (type.equals("scale")) {
            return Scale.fromString(value);
        } else if (type.startsWith("model.")) {
            return this.convertModelValue(diagram);
        } else {
            // it didn't recognize the type
            LOG.warning("Not recognized type: " + type);
            return null;
        }
    }

    private Object convertModelValue(TrainDiagram diagram) {
        if (diagram == null) {
            LOG.warning("Cannot convert model value without diagram.");
            return null;
        } else {
            if (type.equals("model.engine.class")) {
                return diagram.getEngineClassById(value);
            } else if (type.equals("model.line.class")) {
                return diagram.getNet().getLineClassById(value);
            } else {
                LOG.warning("Not recognized model type: " + type);
                return null;
            }
        }
    }

    private Pair<String, String> convertToId(ObjectWithId object) {
        String lKey = null;
        String lValue = null;
        if (object instanceof EngineClass) {
            lKey = "model.engine.class";
            lValue = object.getId();
        } else if (object instanceof LineClass) {
            lKey = "model.line.class";
            lValue = object.getId();
        } else {
            LOG.warning("Not recognized class: " + object.getClass().getName());
        }
        return new Pair<String, String>(lKey, lValue);
    }

    @Override
    public String toString() {
        return String.format("item(%s,%s,%s)", key, type, value);
    }
}
