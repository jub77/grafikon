/*
 * LSTrainsCycleItem.java
 * 
 * Created on 15.9.2007, 20:58:51
 */
package net.parostroj.timetable.model.save.version02;

import net.parostroj.timetable.model.TrainsCycleItem;

/**
 * Cycle item.
 * 
 * @author jub
 */
public class LSTrainsCycleItem {
    
    private int trainId;
    
    private String comment;
    
    private Integer sourceId;
    
    private Integer targetId;

    public LSTrainsCycleItem() {
    }

    public LSTrainsCycleItem(TrainsCycleItem item, LSTransformationData data) {
        this.trainId = data.getIdForObject(item.getTrain());
        this.comment = item.getComment();
        if (item.getFrom() != null)
            this.sourceId = data.getIdForObject(item.getFrom());
        if (item.getTo() != null)
            this.targetId = data.getIdForObject(item.getFrom());
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }
}
