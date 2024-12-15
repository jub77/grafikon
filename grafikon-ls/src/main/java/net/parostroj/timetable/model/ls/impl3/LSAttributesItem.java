package net.parostroj.timetable.model.ls.impl3;

import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Scale;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One item for LSAttributes.
 *
 * @author jub
 */
@XmlType(propOrder = {"key", "value", "type"})
public class LSAttributesItem {

    private static final Logger log = LoggerFactory.getLogger(LSAttributesItem.class);

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
        switch (value) {
            case String s -> {
                this.value = s;
                this.type = "string";
            }
            case Boolean b -> {
                this.value = b.toString();
                this.type = "boolean";
            }
            case Integer i -> {
                this.value = i.toString();
                this.type = "integer";
            }
            case Double v -> {
                this.value = v.toString();
                this.type = "double";
            }
            case Scale scale -> {
                this.value = scale.toString();
                this.type = "scale";
            }
            case ObjectWithId objectWithId -> {
                Pair<String, String> pair = this.convertToId(objectWithId);
                this.type = pair.first;
                this.value = pair.second;
            }
            case null, default -> log.warn("Cannot convert value to string: {}", key);
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
            log.warn("Not recognized type: {}", type);
            return null;
        }
    }

    private Object convertModelValue(TrainDiagram diagram) {
        if (diagram == null) {
            log.warn("Cannot convert model value without diagram.");
            return null;
        } else {
            if (type.equals("model.engine.class")) {
                return diagram.getEngineClasses().getById(value);
            } else if (type.equals("model.line.class")) {
                return diagram.getNet().getLineClasses().getById(value);
            } else {
                log.warn("Not recognized model type: {}", type);
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
            log.warn("Not recognized class: {}", object.getClass().getName());
        }
        return new Pair<>(lKey, lValue);
    }

    @Override
    public String toString() {
        return String.format("item(%s,%s,%s)", key, type, value);
    }
}
