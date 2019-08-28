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

    // deprecate - loaded for backward compatibility
    private String fromStraightTrack;
    // deprecate - loaded for backward compatibility
    private String toStraightTrack;

    public LSLineTrack(LineTrack track) {
        super(track);
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
