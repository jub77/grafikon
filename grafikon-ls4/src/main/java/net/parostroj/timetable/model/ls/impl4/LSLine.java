package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for storing lines.
 *
 * @author jub
 */
@XmlRootElement(name = "line")
@XmlType(name = "line", propOrder = {"id", "length", "speed", "from", "to", "attributes", "tracks"})
public class LSLine {

    private String id;
    private int length;
    private Integer speed;
    private String from;
    private String to;
    private LSAttributes attributes;
    private List<LSLineTrack> tracks;

    public LSLine(Line line) {
        this.id = line.getId();
        this.length = line.getLength();
        this.speed = line.getTopSpeed();
        this.from = line.getFrom().getId();
        this.to = line.getTo().getId();
        this.attributes = new LSAttributes(line.getAttributes());
        this.tracks = new LinkedList<>();
        for (LineTrack track : line.getTracks()) {
            this.tracks.add(new LSLineTrack(track));
        }
    }

    public LSLine() {
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    @XmlElementWrapper
    @XmlElement(name = "track")
    public List<LSLineTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<LSLineTrack> tracks) {
        this.tracks = tracks;
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

    public Line createLine(TrainDiagram diagram) throws LSException {
        Net net = diagram.getNet();
        Node fromNode = net.getNodeById(getFrom());
        Node toNode = net.getNodeById(getTo());
        if (speed != null && speed <= 0) {
            speed = null;
        }
        Line line = diagram.getPartFactory().createLine(id, length, fromNode, toNode, speed);
        line.getAttributes().add(attributes.createAttributes(diagram::getObjectById));
        // tracks
        if (this.tracks != null)
            for (LSLineTrack lsLineTrack : this.tracks) {
                LineTrack lineTrack = lsLineTrack.createLineTrack(diagram::getObjectById);
                NodeTrack fromStraight = fromNode.getTrackById(lsLineTrack.getFromStraightTrack());
                NodeTrack toStraight = toNode.getTrackById(lsLineTrack.getToStraightTrack());
                lineTrack.setFromStraightTrack(fromStraight);
                lineTrack.setToStraightTrack(toStraight);
                line.addTrack(lineTrack);
            }
        return line;
    }
}
