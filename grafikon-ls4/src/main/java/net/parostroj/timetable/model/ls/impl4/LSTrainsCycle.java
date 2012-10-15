package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for train cycles.
 * 
 * @author jub
 */
@XmlRootElement(name = "trains_cycle")
@XmlType(propOrder = {"id", "name", "description", "type", "attributes", "items"})
public class LSTrainsCycle {

    private String id;
    private String name;
    private String description;
    private String type;
    private LSAttributes attributes;
    private List<LSTrainsCycleItem> items;

    public LSTrainsCycle() {
    }

    public LSTrainsCycle(TrainsCycle cycle) {
        this.id = cycle.getId();
        this.name = cycle.getName();
        this.description = cycle.getDescription();
        this.type = cycle.getType().getName();
        this.attributes = new LSAttributes(cycle.getAttributes());
        this.items = new LinkedList<LSTrainsCycleItem>();
        for (TrainsCycleItem item : cycle) {
            items.add(new LSTrainsCycleItem(item));
        }
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElementWrapper
    @XmlElement(name = "item")
    public List<LSTrainsCycleItem> getItems() {
        return items;
    }

    public void setItems(List<LSTrainsCycleItem> items) {
        this.items = items;
    }
    
    public TrainsCycle createTrainsCycle(TrainDiagram diagram) throws LSException {
        TrainsCycle cycle = new TrainsCycle(id, name, description, diagram.getCyclesType(type));
        cycle.setAttributes(attributes.createAttributes(diagram));
        for (LSTrainsCycleItem item : items) {
            cycle.addItem(item.createTrainsCycleItem(cycle, diagram));
        }
        return cycle;
    }
}
