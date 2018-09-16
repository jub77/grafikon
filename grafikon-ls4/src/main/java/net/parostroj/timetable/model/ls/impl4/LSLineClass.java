package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for line class.
 *
 * @author jub
 */
@XmlRootElement(name = "line_class")
@XmlType(propOrder = {"id", "name", "attributes"})
public class LSLineClass {

    private String id;
    private String name;
    private LSAttributes attributes;

    public LSLineClass(LineClass lineClass) {
        this.id = lineClass.getId();
        this.name = lineClass.getName();
        this.attributes = new LSAttributes(lineClass.getAttributes());
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

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public LineClass createLineClass() throws LSException {
        LineClass lineClass = new LineClass(id);
        lineClass.setName(name);
        if (this.attributes != null) {
            lineClass.getAttributes().add(this.attributes.createAttributes());
        }
        return lineClass;
    }
}
