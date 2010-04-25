package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Text.
 *
 * @author jub
 */
public class Text {

    private String name;
    private String type;
    private String text;

    public Text() {
    }

    public Text(String name, String type, String text) {
        this.name = name;
        this.type = type;
        this.text = text;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlValue
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
