package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for storing group.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "attributes"})
public class LSGroup {

    private String id;
    private String name;
    private LSAttributes attributes;

    public LSGroup() {
    }

    public LSGroup(Group group) {
        this.setId(group.getId());
        this.setName(group.getName());
        this.setAttributes(new LSAttributes(group.getAttributes()));
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

    public Group createGroup(TrainDiagram diagram) throws LSException {
        Group group = diagram.createGroup(id);
        group.setName(name);
        group.setAttributes(attributes.createAttributes(diagram));
        return group;
    }
}
