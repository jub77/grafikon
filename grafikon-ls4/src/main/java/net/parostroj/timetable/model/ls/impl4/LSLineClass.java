package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.LineClass;

/**
 * Storage for line class.
 * 
 * @author jub
 */
@XmlType(propOrder = {"id", "name"})
public class LSLineClass {

    private String id;
    private String name;

    public LSLineClass(LineClass lineClass) {
        this.id = lineClass.getId();
        this.name = lineClass.getName();
    }

    public LSLineClass() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public LineClass createLineClass() {
        LineClass lineClass = new LineClass(id, name);
        return lineClass;
    }
}
