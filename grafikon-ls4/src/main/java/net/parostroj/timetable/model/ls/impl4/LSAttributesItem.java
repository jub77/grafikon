package net.parostroj.timetable.model.ls.impl4;

import java.util.*;

import javax.xml.bind.annotation.XmlElement;
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
@XmlType(propOrder = {"key", "values", "type", "category"})
public class LSAttributesItem {

    private static final Logger log = LoggerFactory.getLogger(LSAttributesItem.class);

    private String key;
    private List<LSAttributesValue> values;
    private String type;
    private String category;

    /**
     * Default constructor.
     */
    public LSAttributesItem() {
    }

    public LSAttributesItem(String key, Object value, String category) {
        this.key = key;
        this.category = category;
        if (value instanceof Set || value instanceof List) {
            type = value instanceof Set ? "set" : "list";
            for (Object item : (Collection<?>) value) {
                LSAttributesValue cValue = extractSimpleValue(key, item);
                this.getValues().add(cValue);
            }
        } else {
            LSAttributesValue cValue = extractSimpleValue(key, value);
            if (value != null) {
                this.getValues().add(cValue);
            }
        }
    }

    private LSAttributesValue extractSimpleValue(String key, Object value) {
        LSAttributesValue cValue = null;
        if (value instanceof String) {
            cValue = new LSAttributesValue((String) value, "string");
        } else if (value instanceof Boolean) {
            cValue = new LSAttributesValue(value.toString(), "boolean");
        } else if (value instanceof Integer) {
            cValue = new LSAttributesValue(value.toString(), "integer");
        } else if (value instanceof Long) {
            cValue = new LSAttributesValue(value.toString(), "long");
        } else if (value instanceof Float) {
            cValue = new LSAttributesValue(value.toString(), "float");
        } else if (value instanceof Double) {
            cValue = new LSAttributesValue(value.toString(), "double");
        } else if (value instanceof Scale) {
            cValue = new LSAttributesValue(value.toString(), "scale");
        } else if (value instanceof FreightColor) {
            cValue = new LSAttributesValue(((FreightColor) value).getKey(), "freight.color");
        } else if (value instanceof LengthUnit) {
            cValue = new LSAttributesValue(((LengthUnit) value).getKey(), "length.unit");
        } else if (value instanceof WeightUnit) {
            cValue = new LSAttributesValue(((WeightUnit) value).getKey(), "weight.unit");
        } else if (value instanceof TextTemplate) {
            TextTemplate tt = (TextTemplate) value;
            cValue = new LSAttributesValue(tt.getTemplate(), "text.template." + tt.getLanguage().name());
        } else if (value instanceof ObjectWithId) {
            Pair<String, String> pair = this.convertToId((ObjectWithId) value);
            cValue = new LSAttributesValue(pair.second, pair.first);
        } else {
            log.warn("Cannot convert value to string: {}", key);
        }
        return cValue;
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

    @XmlElement(name = "value")
    public List<LSAttributesValue> getValues() {
        if (values == null) {
            values = new ArrayList<LSAttributesValue>();
        }
        return values;
    }

    public void setValues(List<LSAttributesValue> values) {
        this.values = values;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Object convertValue(TrainDiagram diagram) throws LSException {
        if ("set".equals(type) || "list".equals(type)) {
            Collection<Object> result = null;
            if (type.equals("set")) {
                result = new HashSet<Object>();
            } else {
                result = new ArrayList<Object>();
            }
            for (LSAttributesValue value : getValues()) {
                result.add(convertSimpleValue(diagram, value.getValue(), value.getType()));
            }
            return result;
        } else {
            LSAttributesValue value = getValues().isEmpty() ? null : getValues().get(0);
            return value == null ? null : convertSimpleValue(diagram, value.getValue(), value.getType() == null ? type : value.getType());
        }
    }

    private Object convertSimpleValue(TrainDiagram diagram, String value, String valueType) throws LSException {
        if (valueType == null) {
            return null;
        } else if (valueType.equals("string")) {
            return value;
        } else if (valueType.equals("boolean")) {
            return Boolean.valueOf(value);
        } else if (valueType.equals("integer")) {
            return Integer.valueOf(value);
        } else if (valueType.equals("double")) {
            return Double.valueOf(value);
        } else if (valueType.equals("long")) {
            return Long.valueOf(value);
        } else if (valueType.equals("float")) {
            return Float.valueOf(value);
        } else if (valueType.equals("scale")) {
            return Scale.fromString(value);
        } else if (valueType.equals("length.unit")) {
            return LengthUnit.getByKey(value);
        } else if (valueType.equals("weight.unit")) {
            return WeightUnit.getByKey(value);
        } else if (valueType.equals("freight.color")) {
            return FreightColor.getByKey(value);
        } else if (valueType.startsWith("text.template.")) {
            return this.convertTextTemplate(value, valueType);
        } else if (valueType.startsWith("model.")) {
            return this.convertModelValue(diagram, value, valueType);
        } else {
            // it didn't recognize the type
            log.warn("Not recognized type: {}", valueType);
            return null;
        }
    }

    private Object convertTextTemplate(String value, String valueType) throws LSException {
        String languageStr = valueType.substring("text.template.".length());
        TextTemplate.Language language = TextTemplate.Language.valueOf(languageStr);
        try {
            return TextTemplate.createTextTemplate(value, language);
        } catch (GrafikonException e) {
            throw new LSException("Cannot convert template: " + e.getMessage(), e);
        }
    }

    private Object convertModelValue(TrainDiagram diagram, String value, String valueType) {
        if (diagram == null) {
            log.warn("Cannot convert model value without diagram.");
            return null;
        } else {
            if (valueType.equals("model.engine.class")) {
                return diagram.getEngineClassById(value);
            } else if (valueType.equals("model.line.class")) {
                return diagram.getNet().getLineClassById(value);
            } else if (valueType.equals("model.object")) {
                return diagram.getObjectById(value);
            } else {
                log.warn("Not recognized model type: {}", valueType);
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
            lKey = "model.object";
            lValue = object.getId();
        }
        return new Pair<String, String>(lKey, lValue);
    }

    @Override
    public String toString() {
        return String.format("item(%s,%s,%s,%s)", key, type, values);
    }
}
