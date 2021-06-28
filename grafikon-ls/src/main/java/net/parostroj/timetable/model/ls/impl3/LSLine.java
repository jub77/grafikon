package net.parostroj.timetable.model.ls.impl3;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Class for storing lines.
 *
 * @author jub
 */
@XmlRootElement(name = "line")
@XmlType(propOrder = {"id", "length", "speed", "from", "to", "attributes", "tracks"})
public class LSLine {

    private String id;
    private int length;
    private int speed;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
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

    public Line createLine(TrainDiagram diagram) {
        Net net = diagram.getNet();
        Node fromNode = net.getNodeById(getFrom());
        Node toNode = net.getNodeById(getTo());
        TrainDiagramPartFactory factory = diagram.getPartFactory();
        Line line = factory.createLine(id);
        line.setLength(length);
        line.setTopSpeed(speed > 0 ? speed : null);
        line.getAttributes().add(attributes.createAttributes(diagram));
        // tracks
        for (LSLineTrack lsLineTrack : getTracks()) {
            LineTrack lineTrack = lsLineTrack.createLineTrack(line);
            NodeTrack fromStraight = fromNode.getTrackById(lsLineTrack.getFromStraightTrack());
            NodeTrack toStraight = toNode.getTrackById(lsLineTrack.getToStraightTrack());
            line.getTracks().add(lineTrack);
            this.createConnectors(diagram, fromNode, toNode, fromStraight, toStraight, lineTrack);
        }
        diagram.getNet().addLine(line, fromNode, toNode);
        return line;
    }

    private void createConnectors(TrainDiagram diagram, Node fromNode, Node toNode,
            NodeTrack fromNt, NodeTrack toNt, LineTrack lineTrack) {
        TrackConnector fromConn = diagram.getPartFactory().createDefaultConnector(
                IdGenerator.getInstance().getId(), fromNode, "2", Node.Side.RIGHT,
                Optional.ofNullable(fromNt));
        fromConn.setLineTrack(Optional.ofNullable(lineTrack));
        fromNode.getConnectors().add(fromConn);
        TrackConnector toConn = diagram.getPartFactory().createDefaultConnector(
                IdGenerator.getInstance().getId(), toNode, "1", Node.Side.LEFT,
                Optional.ofNullable(toNt));
        toConn.setLineTrack(Optional.ofNullable(lineTrack));
        toNode.getConnectors().add(toConn);
    }
}
