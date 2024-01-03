package net.parostroj.timetable.model.ls.impl4;

import java.util.*;
import java.util.function.Function;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.LocalizedString.StringWithLocale;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.WeightUnit;
import net.parostroj.timetable.utils.CollectionUtils;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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
    private static final String MAP_KEY = "map";
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

    private static final BiMap<Class<?>, String> ENUM_TYPE_MAP = HashBiMap.create();

    static {
        ENUM_TYPE_MAP.put(NodeType.class, "node.type");
        ENUM_TYPE_MAP.put(Node.Side.class, "node.side");
    }

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
        } else if (value instanceof Map) {
            type = MAP_KEY;
            for (Map.Entry<?, ?> entry : ((Map<?,?>) value).entrySet()) {
                LSAttributesValue cKey = extractSimpleValue(key, entry.getKey());
                this.getValues().add(cKey);
                LSAttributesValue cValue = extractSimpleValue(key, entry.getValue());
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
        } else if (value instanceof Location) {
            Location loc = (Location) value;
            cValue = new LSAttributesValue(String.format("%d;%d", loc.getX(), loc.getY()),
                    "location");
        } else if (value instanceof Enum<?>) {
            Enum<?> e = (Enum<?>) value;
            String type = ENUM_TYPE_MAP.get(e.getDeclaringClass());
            if (type != null) {
                cValue = new LSAttributesValue(((Enum<?>) value).name(), "enum." + type);
            } else {
                log.warn("Unknown enum type: {}", e.getDeclaringClass().getName());
            }
        } else {
            log.warn("Cannot convert value to string: {} ({})", key, value);
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
            values = new ArrayList<>();
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

    public Object convertValue(Function<String, ObjectWithId> mapping) throws LSException {
        if (SET_KEY.equals(type) || LIST_KEY.equals(type)) {
            Collection<Object> result = null;
            if (type.equals(SET_KEY)) {
                result = new HashSet<>();
            } else {
                result = new ArrayList<>();
            }
            for (LSAttributesValue lsValue : getValues()) {
                Object value = convertSimpleValue(mapping, lsValue.getValue(), lsValue.getType());
                if (value != null) {
                    result.add(value);
                } else {
                    log.warn("Null value in collection - type: {}, value: {}", lsValue.getType(), lsValue.getValue());
                }
            }
            return result;
        } else if (MAP_KEY.equals(type)) {
            Map<Object, Object> result = new HashMap<>();
            for (Tuple<LSAttributesValue> t : CollectionUtils.tuples(getValues())) {
                Object key = convertSimpleValue(mapping, t.first.getValue(), t.first.getType());
                Object value = convertSimpleValue(mapping, t.second.getValue(), t.second.getType());
                result.put(key, value);
            }
            return result;
        } else if (LOCALIZED_STRING_KEY.equals(type)) {
            LocalizedString.Builder builder = LocalizedString.newBuilder();
            for (LSAttributesValue value : getValues()) {
                Object lString = convertSimpleValue(mapping, value.getValue(), value.getType());
                if (lString instanceof String) {
                    builder.setDefaultString((String) lString);
                } else if (lString instanceof StringWithLocale) {
                    builder.addStringWithLocale((StringWithLocale) lString);
                }
            }
            return builder.build();
        } else {
            LSAttributesValue value = getValues().isEmpty() ? null : getValues().get(0);
            return value == null ? null : convertSimpleValue(mapping, value.getValue(), value.getType() == null ? type : value.getType());
        }
    }

    private Object convertSimpleValue(Function<String, ObjectWithId> mapping, String value, String valueType) throws LSException {
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
            return this.convertModelValue(mapping, value, valueType);
        } else if (valueType.startsWith("string.")) {
            return this.convertToStringWithLocale(value, valueType);
        } else if (valueType.equals("location")) {
            return this.convertLocation(value);
        } else if (valueType.startsWith("enum.")) {
            return this.convertEnumValue(value, valueType);
        } else {
            // it didn't recognize the type
            log.warn("Not recognized type: {}", valueType);
            return null;
        }
    }

    private Object convertLocation(String value) {
        String[] locationParts = value.split(";");
        int x = Integer.parseInt(locationParts[0]);
        int y = Integer.parseInt(locationParts[1]);
        return new Location(x, y);
    }

    private Object convertEnumValue(String value, String valueType) {
        Class<?> enumCls = ENUM_TYPE_MAP.inverse().get(valueType.substring("enum.".length()));
        Object[] enumConstants = enumCls.getEnumConstants();
        for (Object ec : enumConstants) {
            Enum<?> ev = (Enum<?>) ec;
            if (ev.name().equals(value)) {
                return ev;
            }
        }
        log.warn("Unknow enum value ({}): {}", valueType, value);
        return null;
    }

    private Object convertToStringWithLocale(String value, String valueType) {
        String languageTag = valueType.substring("string.".length());
        return LocalizedString.newStringWithLocale(value, Locale.forLanguageTag(languageTag));
    }

    private Object convertTextTemplate(String value, String valueType) throws LSException {
        String languageStr = valueType.substring(TEXT_TEMPLATE_KEY_PREFIX.length());
        TextTemplate.Language language = TextTemplate.Language.fromString(languageStr);
        try {
            return TextTemplate.createTextTemplate(language == null ? "" : value,
                    language == null ? TextTemplate.Language.GROOVY : language);
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

    private Object convertModelValue(Function<String, ObjectWithId> mapping, String value, String valueType) {
        if (mapping == null) {
            log.warn("Cannot convert model value without id mapping.");
            return null;
        } else {
            if (valueType.equals(ENGINE_CLASS_KEY)
                    || valueType.equals(LINE_CLASS_KEY) || valueType.equals(MODEL_OBJECT_KEY)) {
                return mapping.apply(value);
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
        return new Pair<>(lKey, lValue);
    }

    @Override
    public String toString() {
        return String.format("item(%s,%s,%s)", key, type, values);
    }
}
