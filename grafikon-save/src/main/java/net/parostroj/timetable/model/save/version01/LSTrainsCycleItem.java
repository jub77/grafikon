/*
 * LSTrainsCycleItem.java
 * 
 * Created on 15.9.2007, 20:58:51
 */

package net.parostroj.timetable.model.save.version01;

/**
 * Cyclee item.
 * 
 * @author jub
 */
public class LSTrainsCycleItem {
    
    private int trainId;
    
    private String comment;

    public LSTrainsCycleItem() {
    }

    public LSTrainsCycleItem(int trainId, String comment) {
        this.trainId = trainId;
        this.comment = comment;
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
}
