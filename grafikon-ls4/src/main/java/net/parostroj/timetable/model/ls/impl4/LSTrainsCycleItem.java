package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for trains cycle item.
 *
 * @author jub
 */
@XmlType(propOrder = {"train", "comment", "from", "to", "attributes"})
public class LSTrainsCycleItem {

    private String train;
    private String comment;
    private String from;
    private String to;
    private LSAttributes attributes;

    public LSTrainsCycleItem() {
    }

    public LSTrainsCycleItem(TrainsCycleItem item) {
        this.train = item.getTrain().getId();
        this.comment = item.getComment();
        if (item.getFrom() != null) {
            this.from = item.getFrom().getId();
        }
        if (item.getTo() != null) {
            this.to = item.getTo().getId();
        }
        this.attributes = new LSAttributes(item.getAttributes());
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

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public TrainsCycleItem createTrainsCycleItem(TrainsCycle cycle, TrainDiagram diagram) throws LSException {
        Train modelTrain = diagram.getTrains().getById(train);
        TrainsCycleItem item = new TrainsCycleItem(cycle, modelTrain,
                comment, modelTrain.getIntervalById(from), modelTrain.getIntervalById(to));
        if (attributes != null) {
            item.getAttributes().add(attributes.createAttributes(diagram));
        }
        return item;
    }
}
