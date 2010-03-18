/*
 * LSEngineCycle.java
 * 
 * Created on 12.9.2007, 13:00:29
 */

package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;

/**
 * Storage object for engine cycle.
 * 
 * @author jub
 */
public class LSTrainsCycle {
    
    private String name;
    
    private String description;
    
    private String comment;
    
    private LSTrainsCycleItem[] items;

    private String type;

    public LSTrainsCycle(TrainsCycle trainsCycle, LSTransformationData data, TrainsCycleType type) {
        this.name = trainsCycle.getName();
        this.description = trainsCycle.getDescription();
        this.comment = (String)trainsCycle.getAttribute("comment");
        
        items = new LSTrainsCycleItem[trainsCycle.getItems().size()];
        int i = 0;
        for (TrainsCycleItem item : trainsCycle) {
            items[i++] = new LSTrainsCycleItem(data.getIdForObject(item.getTrain()),item.getComment());
        }
        this.type = type.name();
    }
    
    public LSTrainsCycle() {}

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LSTrainsCycleItem[] getItems() {
        return items;
    }

    public void setItems(LSTrainsCycleItem[] items) {
        this.items = items;
    }
    
    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }
}
