package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.WeightUnit;
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

    private static final Logger LOG = LoggerFactory.getLogger(LSAttributesItem.class.getName());
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
        } else if (value instanceof LengthUnit) {
            this.value = ((LengthUnit) value).getKey();
            this.type = "length.unit";
        } else if (value instanceof WeightUnit) {
            this.value = ((WeightUnit) value).getKey();
            this.type = "length.unit";
        } else if (value instanceof TextTemplate) {
            TextTemplate tt = (TextTemplate) value;
            this.type = "text.template." + tt.getLanguage().name();
            this.value = tt.getTemplate();
        } else if (value instanceof ObjectWithId) {
            Pair<String, String> pair = this.convertToId((ObjectWithId) value);
            this.type = pair.first;
            this.value = pair.second;
        } else {
            LOG.warn("Cannot convert value to string: {}", key);
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

    public Object convertValue(TrainDiagram diagram) throws LSException {
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
        } else if (type.equals("length.unit")) {
            return LengthUnit.getByKey(value);
        } else if (type.equals("weight.unit")) {
            return WeightUnit.getByKey(value);
        } else if (type.startsWith("text.template.")) {
            return this.convertTextTemplate();
        } else if (type.startsWith("model.")) {
            return this.convertModelValue(diagram);
        } else {
            // it didn't recognize the type
            LOG.warn("Not recognized type: {}", type);
            return null;
        }
    }

    private Object convertTextTemplate() throws LSException {
        String languageStr = type.substring("text.template.".length());
        TextTemplate.Language language = TextTemplate.Language.valueOf(languageStr);
        try {
            return TextTemplate.createTextTemplate(value, language);
        } catch (GrafikonException e) {
            throw new LSException("Cannot convert template: " + e.getMessage(), e);
        }
    }

    private Object convertModelValue(TrainDiagram diagram) {
        if (diagram == null) {
            LOG.warn("Cannot convert model value without diagram.");
            return null;
        } else {
            if (type.equals("model.engine.class")) {
                return diagram.getEngineClassById(value);
            } else if (type.equals("model.line.class")) {
                return diagram.getNet().getLineClassById(value);
            } else {
                LOG.warn("Not recognized model type: {}", type);
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
            LOG.warn("Not recognized class: {}", object.getClass().getName());
        }
        return new Pair<String, String>(lKey, lValue);
    }

    @Override
    public String toString() {
        return String.format("item(%s,%s,%s)", key, type, value);
    }
}
