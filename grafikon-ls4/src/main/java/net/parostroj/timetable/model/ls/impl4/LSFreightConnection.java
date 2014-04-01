package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.FreightNet;

/**
 * Freight connection.
 *
 * @author jub
 */
@XmlType(propOrder = { "trainFrom", "trainTo", "intervalFrom", "intervalTo", "attributes" })
public class LSFreightConnection {

    private String trainFrom;
    private String trainTo;
    private String intervalFrom;
    private String intervalTo;
    private LSAttributes attributes;

    public LSFreightConnection() {
    }

    public LSFreightConnection(FreightNet.FNConnection connection) {
        this.trainFrom = connection.getFrom().getTrain().getId();
        this.trainTo = connection.getTo().getTrain().getId();
        this.intervalFrom = connection.getFrom().getId();
        this.intervalTo = connection.getTo().getId();
        this.attributes = new LSAttributes(connection);
    }

    public String getTrainFrom() {
        return trainFrom;
    }

    public void setTrainFrom(String trainFrom) {
        this.trainFrom = trainFrom;
    }

    public String getTrainTo() {
        return trainTo;
    }

    public void setTrainTo(String trainTo) {
        this.trainTo = trainTo;
    }

    public String getIntervalFrom() {
        return intervalFrom;
    }

    public void setIntervalFrom(String intervalFrom) {
        this.intervalFrom = intervalFrom;
    }

    public String getIntervalTo() {
        return intervalTo;
    }

    public void setIntervalTo(String intervalTo) {
        this.intervalTo = intervalTo;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }
}
