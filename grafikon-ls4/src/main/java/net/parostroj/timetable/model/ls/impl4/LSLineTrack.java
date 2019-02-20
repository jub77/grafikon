package net.parostroj.timetable.model.ls.impl4;

import java.util.function.Function;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for line track.
 *
 * @author jub
 */
@XmlType(propOrder = {"fromStraightTrack", "toStraightTrack"})
public class LSLineTrack extends LSTrack {

    private String fromStraightTrack;
    private String toStraightTrack;

    public LSLineTrack(LineTrack track) {
        super(track);
        this.fromStraightTrack = (track.getFromStraightTrack() != null) ? track.getFromStraightTrack().getId() : null;
        this.toStraightTrack = (track.getToStraightTrack() != null) ? track.getToStraightTrack().getId() : null;
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

    public LineTrack createLineTrack(Line line, Function<String, ObjectWithId> mapping) throws LSException {
        LineTrack lineTrack = new LineTrack(this.getId(), line);
        this.addValuesTrack(mapping, lineTrack);
        return lineTrack;
    }
}
