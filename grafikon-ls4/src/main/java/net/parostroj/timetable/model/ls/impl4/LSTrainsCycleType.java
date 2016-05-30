package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for type of trains cycles.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "key", "attributes"})
public class LSTrainsCycleType {

    private String id;
    // not used anymore for serialization
    private String name;
    private String key;
    private LSAttributes attributes;

    public LSTrainsCycleType() {}

    public LSTrainsCycleType(TrainsCycleType type) {
        this.id = type.getId();
        this.key = type.getKey();
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TrainsCycleType createTrainsCycleType(TrainDiagram diagram) throws LSException {
        TrainsCycleType type = new TrainsCycleType(id, diagram);
        type.setKey(key);
        if (name != null) {
            type.setKey(name);
            type.setName(LocalizedString.fromString(name));
        }
        type.getAttributes().add(attributes.createAttributes(diagram));
        return type;
    }
}
