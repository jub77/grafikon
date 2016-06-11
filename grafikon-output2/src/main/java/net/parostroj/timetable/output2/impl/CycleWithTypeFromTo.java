package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;

public class CycleWithTypeFromTo extends CycleFromTo {

    private String typeKey;
    private LocalizedString typeName;

    public CycleWithTypeFromTo() {
    }

    public CycleWithTypeFromTo(boolean start, boolean in, String name, String desc, String trainName, String time) {
        super(start, in, name, desc, trainName, time);
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getTypeName() {
        return typeName;
    }

    public void setTypeName(LocalizedString typeName) {
        this.typeName = typeName;
    }
}
