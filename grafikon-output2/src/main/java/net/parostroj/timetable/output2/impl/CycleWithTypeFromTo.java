package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlAttribute;

public class CycleWithTypeFromTo extends CycleFromTo {

    private String type;

    public CycleWithTypeFromTo() {
    }

    public CycleWithTypeFromTo(boolean in, String name, String desc, String trainName, String time, String type) {
        super(in, name, desc, trainName, time);
        this.type = type;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
