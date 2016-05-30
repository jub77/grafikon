package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Storage for train cycles.
 *
 * @author jub
 */
@XmlRootElement(name = "trains_cycle")
@XmlType(propOrder = {"id", "name", "description", "type", "next", "attributes", "items"})
public class LSTrainsCycle {

    private String id;
    private String name;
    private String description;
    private String type;
    private String next;
    private LSAttributes attributes;
    private List<LSTrainsCycleItem> items;

    public LSTrainsCycle() {
    }

    public LSTrainsCycle(TrainsCycle cycle) {
        this.id = cycle.getId();
        this.name = cycle.getName();
        this.description = cycle.getDescription();
        this.type = cycle.getType().getId();
        this.attributes = new LSAttributes(cycle.getAttributes());
        if (cycle.isPartOfSequence()) {
            next = cycle.getNext().getId();
        }
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

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
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
        TrainsCycleType cycleType = diagram.getCycleTypes().getById(type);
        if (cycleType == null) {
            // fallback to name
            for (TrainsCycleType t : diagram.getCycleTypes()) {
                if (ObjectsUtil.compareWithNull(t.getKey(), type)) {
                    cycleType = t;
                }
            }
        }
        TrainsCycle cycle = new TrainsCycle(id, diagram, name, description, cycleType);
        cycle.getAttributes().add(attributes.createAttributes(diagram));
        if (this.items != null) {
            for (LSTrainsCycleItem item : this.items) {
                cycle.addItem(item.createTrainsCycleItem(cycle, diagram));
            }
        }
        return cycle;
    }
}
