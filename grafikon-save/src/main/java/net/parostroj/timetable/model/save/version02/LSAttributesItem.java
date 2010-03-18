package net.parostroj.timetable.model.save.version02;

import net.parostroj.timetable.model.Scale;

/**
 * One item for LSAttributes.
 * 
 * @author jub
 */
public class LSAttributesItem {
    
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
            this.value = (String)value;
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
    
    public Object convertValue() {
        if (type == null)
            return null;
        else if (type.equals("string"))
            return value;
        else if (type.equals("boolean"))
            return Boolean.valueOf(value);
        else if (type.equals("integer"))
            return Integer.valueOf(value);
        else if (type.equals("double"))
            return Double.valueOf(value);
        else if (type.equals("scale"))
            return Scale.fromString(value);
        else
            // it didn't recognize the type
            return null;
    }

    @Override
    public String toString() {
        return String.format("item(%s,%s,%s)",key,type,value);
    }
}
