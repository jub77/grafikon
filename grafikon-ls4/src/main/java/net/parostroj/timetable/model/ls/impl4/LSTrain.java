package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.actions.TrainIntervalsBuilder;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Class for storing trains.
 *
 * @author jub
 */
@XmlRootElement(name = "train")
@XmlType(propOrder = {"id", "number", "desc", "type", "topSpeed", "start",
    "timeBefore", "timeAfter", "attributes", "route"})
public class LSTrain {

    private String id;
    private String number;
    private String desc;
    private String type;
    private Integer topSpeed;
    private int start;
    private LSAttributes attributes;
    private List<Object> route;
    private int timeBefore;
    private int timeAfter;

    public LSTrain() {
    }

    public LSTrain(Train train) {
        this.id = train.getId();
        this.number = train.getNumber();
        this.desc = train.getDescription();
        this.type = train.getType() != null ? train.getType().getId() : null;
        this.topSpeed = train.getTopSpeed();
        this.start = train.getStartTime();
        this.timeBefore = train.getTimeBefore();
        this.timeAfter = train.getTimeAfter();
        this.attributes = new LSAttributes(train.getAttributes());

        // create route parts ...
        route = new LinkedList<>();
        for (TimeInterval interval : train.getTimeIntervalList()) {
            Object part;
            if (interval.isLineOwner()) {
                part = new LSTrainRoutePartLine(interval);
            } else {
                part = new LSTrainRoutePartNode(interval);
            }
            route.add(part);
        }
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @XmlElement(name = "top_speed")
    public Integer getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(Integer topSpeed) {
        this.topSpeed = topSpeed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "time_after")
    public int getTimeAfter() {
        return timeAfter;
    }

    public void setTimeAfter(int timeAfter) {
        this.timeAfter = timeAfter;
    }

    @XmlElement(name = "time_before")
    public int getTimeBefore() {
        return timeBefore;
    }

    public void setTimeBefore(int timeBefore) {
        this.timeBefore = timeBefore;
    }

    @XmlElementWrapper
    @XmlElements({
        @XmlElement(name = "node", type = LSTrainRoutePartNode.class),
        @XmlElement(name = "line", type = LSTrainRoutePartLine.class)
    })
    public List<Object> getRoute() {
        return route;
    }

    public void setRoute(List<Object> route) {
        this.route = route;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public DelayedAttributes<Train> createTrain(LSContext context) {
        TrainDiagram diagram = context.getDiagram();
        Train train = diagram.getPartFactory().createTrain(id);
        train.setNumber(number);
        train.setDescription(desc);
        train.setTopSpeed(topSpeed);
        train.setType(diagram.getTrainTypes().getById(type));
        // build time interval list
        TrainIntervalsBuilder builder = new TrainIntervalsBuilder(train, start);
        if (this.route != null) {
            for (Object routePart : this.route) {
                if (routePart instanceof LSTrainRoutePartNode nodePart) {
                    Node node = diagram.getNet().getNodeById(nodePart.getNodeId());
                    NodeTrack nodeTrack = node.getTrackById(nodePart.getTrackId());
                    builder.addNode(nodePart.getIntervalId(), node, nodeTrack, nodePart.getStop(),
                            nodePart.getAttributes().createAttributes(context));
                } else {
                    LSTrainRoutePartLine linePart = (LSTrainRoutePartLine)routePart;
                    Line line = diagram.getNet().getLineById(linePart.getLineId());
                    LineTrack lineTrack = line.getTrackById(linePart.getTrackId());
                    builder.addLine(linePart.getIntervalId(), line, lineTrack, linePart.getSpeed(),
                            linePart.getAddedTime() != null ? linePart.getAddedTime() : 0,
                            linePart.getAttributes().createAttributes(context));
                }
            }
        }
        builder.finish();
        // set technological time
        train.setTimeBefore(this.timeBefore);
        train.setTimeAfter(this.timeAfter);
        return new DelayedAttributes<>(train, attributes);
    }
}
