package net.parostroj.timetable.model.ls.impl4;

import java.util.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.LocalizedString.StringWithLocale;
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
@XmlType(propOrder = {"key", "type", "values", "category"})
public class LSAttributesItem {

    private static final Logger log = LoggerFactory.getLogger(LSAttributesItem.class);

    private static final String SET_KEY = "set";
    private static final String LIST_KEY = "list";
    private static final String LOCALIZED_STRING_KEY = "localized.string";

    private static final String ENGINE_CLASS_KEY = "model.engine.class";
    private static final String LINE_CLASS_KEY = "model.line.class";
    private static final String MODEL_OBJECT_KEY = "model.object";

    private static final String TEXT_TEMPLATE_KEY_PREFIX = "text.template.";
    private static final String SCRIPT_KEY_PREFIX = "script.";

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
            type = value instanceof Set ? SET_KEY : LIST_KEY;
            for (Object item : (Collection<?>) value) {
                LSAttributesValue cValue = extractSimpleValue(key, item);
                this.getValues().add(cValue);
            }
        } else if (value instanceof LocalizedString) {
            type = LOCALIZED_STRING_KEY;
            LocalizedString localizedString = (LocalizedString) value;
            this.getValues().add(extractSimpleValue(key, localizedString.getDefaultString()));
            for (StringWithLocale stringWithLocale : localizedString.getLocalizedStrings()) {
                this.getValues().add(extractSimpleValue(key, stringWithLocale));
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
        } else if (value instanceof Locale) {
            cValue = new LSAttributesValue(((Locale) value).toLanguageTag(), "locale");
        } else if (value instanceof TextTemplate) {
            TextTemplate tt = (TextTemplate) value;
            cValue = new LSAttributesValue(tt.getTemplate(), TEXT_TEMPLATE_KEY_PREFIX + tt.getLanguage().name());
        } else if (value instanceof Script) {
            Script script = (Script) value;
            cValue = new LSAttributesValue(script.getSourceCode(), SCRIPT_KEY_PREFIX + script.getLanguage().name());
        } else if (value instanceof ObjectWithId) {
            Pair<String, String> pair = this.convertToId((ObjectWithId) value);
            cValue = new LSAttributesValue(pair.second, pair.first);
        } else if (value instanceof StringWithLocale) {
            StringWithLocale stringWithLocale = (StringWithLocale) value;
            cValue = new LSAttributesValue(
                    stringWithLocale.getString(),
                    "string." + stringWithLocale.getLocale().toLanguageTag());
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
        if (SET_KEY.equals(type) || LIST_KEY.equals(type)) {
            Collection<Object> result = null;
            if (type.equals(SET_KEY)) {
                result = new HashSet<Object>();
            } else {
                result = new ArrayList<Object>();
            }
            for (LSAttributesValue value : getValues()) {
                result.add(convertSimpleValue(diagram, value.getValue(), value.getType()));
            }
            return result;
        } else if (LOCALIZED_STRING_KEY.equals(type)) {
            LocalizedString.Builder builder = LocalizedString.newBuilder();
            for (LSAttributesValue value : getValues()) {
                Object lString = convertSimpleValue(diagram, value.getValue(), value.getType());
                if (lString instanceof String) {
                    builder.setDefaultString((String) lString);
                } else if (lString instanceof StringWithLocale) {
                    builder.addStringWithLocale((StringWithLocale) lString);
                }
            }
            return builder.build();
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
        } else if (valueType.equals("locale")) {
            return Locale.forLanguageTag(value);
        } else if (valueType.startsWith(TEXT_TEMPLATE_KEY_PREFIX)) {
            return this.convertTextTemplate(value, valueType);
        } else if (valueType.startsWith(SCRIPT_KEY_PREFIX)) {
            return this.convertScript(value, valueType);
        } else if (valueType.startsWith("model.")) {
            return this.convertModelValue(diagram, value, valueType);
        } else if (valueType.startsWith("string.")) {
            return this.convertToStringWithLocale(value, valueType);
        } else {
            // it didn't recognize the type
            log.warn("Not recognized type: {}", valueType);
            return null;
        }
    }

    private Object convertToStringWithLocale(String value, String valueType) {
        String languageTag = valueType.substring("string.".length());
        return LocalizedString.newStringWithLocale(value, Locale.forLanguageTag(languageTag));
    }

    private Object convertTextTemplate(String value, String valueType) throws LSException {
        String languageStr = valueType.substring(TEXT_TEMPLATE_KEY_PREFIX.length());
        TextTemplate.Language language = TextTemplate.Language.valueOf(languageStr);
        try {
            return TextTemplate.createTextTemplate(value, language);
        } catch (GrafikonException e) {
            throw new LSException("Cannot convert template: " + e.getMessage(), e);
        }
    }

    private Object convertScript(String value, String valueType) throws LSException {
        String languageStr = valueType.substring(SCRIPT_KEY_PREFIX.length());
        Script.Language language = Script.Language.valueOf(languageStr);
        try {
            return Script.createScript(value, language);
        } catch (GrafikonException e) {
            throw new LSException("Cannot convert script: " + e.getMessage(), e);
        }
    }

    private Object convertModelValue(TrainDiagram diagram, String value, String valueType) {
        if (diagram == null) {
            log.warn("Cannot convert model value without diagram.");
            return null;
        } else {
            if (valueType.equals(ENGINE_CLASS_KEY)) {
                return diagram.getEngineClasses().getById(value);
            } else if (valueType.equals(LINE_CLASS_KEY)) {
                return diagram.getNet().getLineClasses().getById(value);
            } else if (valueType.equals(MODEL_OBJECT_KEY)) {
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
            lKey = ENGINE_CLASS_KEY;
            lValue = object.getId();
        } else if (object instanceof LineClass) {
            lKey = LINE_CLASS_KEY;
            lValue = object.getId();
        } else {
            lKey = MODEL_OBJECT_KEY;
            lValue = object.getId();
        }
        return new Pair<String, String>(lKey, lValue);
    }

    @Override
    public String toString() {
        return String.format("item(%s,%s,%s,%s)", key, type, values);
    }
}
