package net.parostroj.timetable.model.ls.impl3;

import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;

/**
 * Storage for trains cycle item.
 *
 * @author jub
 */
@XmlType(propOrder = {"train", "comment", "from", "to"})
public class LSTrainsCycleItem {

    private String train;
    private String comment;
    private String from;
    private String to;

    public LSTrainsCycleItem() {
    }

    public LSTrainsCycleItem(TrainsCycleItem item) {
        this.train = item.getTrain().getId();
        if (item.getFrom() != null) {
            this.from = item.getFrom().getId();
        }
        if (item.getTo() != null) {
            this.to = item.getTo().getId();
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }

    public TrainsCycleItem createTrainsCycleItem(TrainsCycle cycle, TrainDiagram diagram) {
        Train modelTrain = diagram.getTrains().getById(train);
        return new TrainsCycleItem(cycle, modelTrain,
                LocalizedString.fromString(comment), modelTrain.getIntervalById(from), modelTrain.getIntervalById(to));
    }
}
