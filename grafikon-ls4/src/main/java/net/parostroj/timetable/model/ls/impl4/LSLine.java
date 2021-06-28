package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Class for storing lines.
 *
 * @author jub
 */
@XmlRootElement(name = "line")
@XmlType(name = "line", propOrder = {"id", "length", "speed", "from", "to", "attributes", "tracks"})
public class LSLine {

    private String id;
    // deprecated
    private Integer length;
    // deprecated
    private Integer speed;
    private String from;
    private String to;
    private LSAttributes attributes;
    private List<LSLineTrack> tracks;

    private int version;

    public LSLine(Line line) {
        this.id = line.getId();
        this.from = line.getFrom().getId();
        this.to = line.getTo().getId();
        this.attributes = new LSAttributes(line.getAttributes());
        this.tracks = new LinkedList<>();
        for (LineTrack track : line.getTracks()) {
            this.tracks.add(new LSLineTrack(track));
        }
        this.version = 1;
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

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
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

    @XmlAttribute
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Line createLine(TrainDiagram diagram) throws LSException {
        Net net = diagram.getNet();
        Node fromNode = net.getNodeById(getFrom());
        Node toNode = net.getNodeById(getTo());
        Line line = diagram.getPartFactory().createLine(id);
        if (this.version == 0) {
            if (speed != null && speed <= 0) {
                speed = null;
            }
            line.setLength(length);
            line.setTopSpeed(speed);
        }
        line.getAttributes().add(attributes.createAttributes(diagram::getObjectById));
        // tracks
        if (this.tracks != null) {
            for (LSLineTrack lsLineTrack : this.tracks) {
                LineTrack lineTrack = lsLineTrack.createLineTrack(line, fromNode, toNode,
                        diagram::getObjectById);
                NodeTrack fromStraight = fromNode.getTrackById(lsLineTrack.getFromStraightTrack());
                NodeTrack toStraight = toNode.getTrackById(lsLineTrack.getToStraightTrack());
                line.getTracks().add(lineTrack);
                if (this.version == 0) {
                    this.createConnectors(diagram, fromNode, toNode, fromStraight, toStraight,
                            lineTrack);
                }
            }
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
