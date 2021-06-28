package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import net.parostroj.timetable.model.changes.Parameter;

/**
 * Storage for parameter of change description.
 *
 * @author jub
 */
public class LSParameter {

    private String value;
    private Boolean trans;

    public LSParameter() {
    }

    public LSParameter(Parameter param) {
        this.value = param.getValue();
        if (param.isTranslated()) {
            this.trans = Boolean.TRUE;
        }
    }

    @XmlAttribute
    public Boolean getTrans() {
        return trans;
    }

    public void setTrans(Boolean trans) {
        this.trans = trans;
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Parameter createParameter() {
        return new Parameter(value, Boolean.TRUE.equals(trans));
    }
}
