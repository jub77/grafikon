package net.parostroj.timetable.model.ls.impl3;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;

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

    public String getFromStraightTrack() {
        return fromStraightTrack;
    }

    public void setFromStraightTrack(String fromStraightTrack) {
        this.fromStraightTrack = fromStraightTrack;
    }

    public String getToStraightTrack() {
        return toStraightTrack;
    }

    public void setToStraightTrack(String toStraightTrack) {
        this.toStraightTrack = toStraightTrack;
    }

    public LineTrack createLineTrack(Line line) {
        LineTrack lineTrack = new LineTrack(this.getId(), line);
        this.addValuesTrack(lineTrack);
        return lineTrack;
    }
}
