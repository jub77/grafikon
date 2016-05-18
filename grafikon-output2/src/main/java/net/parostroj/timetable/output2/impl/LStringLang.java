package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class LStringLang {

    private String lang;
    private String value;

    public LStringLang() {
    }

    public LStringLang(String lang, String value) {
        this.lang = lang;
        this.value = value;
    }

    @XmlAttribute
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
