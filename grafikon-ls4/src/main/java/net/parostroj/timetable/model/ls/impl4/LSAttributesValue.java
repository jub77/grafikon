package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * One value for LSAttributesItem.
 *
 * @author jub
 */
@XmlType
public class LSAttributesValue {

    private String value;
    private String type;

    public LSAttributesValue() {
    }

    public LSAttributesValue(String value, String type) {
        this.value = value;
        this.type = type;
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("<%s,%s>", type, value);
    }
}
