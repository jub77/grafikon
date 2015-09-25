package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for type of trains cycles.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "description", "attributes"})
public class LSTrainsCycleType {

    private String id;
    private String name;
    private String description;
    private LSAttributes attributes;

    public LSTrainsCycleType() {}

    public LSTrainsCycleType(TrainsCycleType type) {
        this.id = type.getId();
        this.name = type.getName();
        this.description = type.getDescription();
        this.attributes = new LSAttributes(type.getAttributes());
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TrainsCycleType createTrainsCycleType(TrainDiagram diagram) throws LSException {
        TrainsCycleType type = new TrainsCycleType(id, diagram);
        type.setName(name);
        type.setDescription(description);
        type.setAttributes(attributes.createAttributes(diagram));
        return type;
    }
}
