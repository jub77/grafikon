package net.parostroj.timetable.model.ls.impl4;

import java.util.Optional;
import java.util.function.Function;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for line track.
 *
 * @author jub
 */
@XmlType(propOrder = { "fromStraightTrack", "toStraightTrack", "fromConnector", "toConnector" })
public class LSLineTrack extends LSTrack {

    // deprecate - loaded for backward compatibility
    private String fromStraightTrack;
    // deprecate - loaded for backward compatibility
    private String toStraightTrack;

    private String fromConnector;
    private String toConnector;

    public LSLineTrack(LineTrack track) {
        super(track);
        this.fromConnector = track.getFromTrackConnector().map(TrackConnector::getId).orElse(null);
        this.toConnector = track.getToTrackConnector().map(TrackConnector::getId).orElse(null);
    }

    public LSLineTrack() {
    }

    @XmlElement(name = "from_straight_track")
    public String getFromStraightTrack() {
        return fromStraightTrack;
    }

    public void setFromStraightTrack(String fromStraightTrack) {
        this.fromStraightTrack = fromStraightTrack;
    }

    @XmlElement(name = "to_straight_track")
    public String getToStraightTrack() {
        return toStraightTrack;
    }

    public void setToStraightTrack(String toStraightTrack) {
        this.toStraightTrack = toStraightTrack;
    }

    public String getFromConnector() {
        return fromConnector;
    }

    public void setFromConnector(String fromConnector) {
        this.fromConnector = fromConnector;
    }

    public String getToConnector() {
        return toConnector;
    }

    public void setToConnector(String toConnector) {
        this.toConnector = toConnector;
    }

    public LineTrack createLineTrack(Line line, Node fromNode, Node toNode,
            Function<String, ObjectWithId> mapping) throws LSException {
        LineTrack lineTrack = new LineTrack(this.getId(), line);
        this.addValuesTrack(mapping, lineTrack);
        if (fromConnector != null) {
            TrackConnector fromC = fromNode.getConnectors().getById(fromConnector);
            fromC.setLineTrack(Optional.ofNullable(lineTrack));
        }
        if (toConnector != null) {
            TrackConnector toC = toNode.getConnectors().getById(toConnector);
            toC.setLineTrack(Optional.ofNullable(lineTrack));
        }
        return lineTrack;
    }
}
