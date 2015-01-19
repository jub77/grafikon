package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

/**
 * Sort pattern group storage.
 * 
 * @author jub
 */
@XmlType(propOrder = {"group", "type"})
public class LSSortPatternGroup {

    private int group;
    private String type;

    public LSSortPatternGroup() {
    }

    public LSSortPatternGroup(int group, String type) {
        this.group = group;
        this.type = type;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
