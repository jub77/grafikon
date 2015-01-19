package net.parostroj.timetable.model.ls.impl3;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.LineTrack;

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

    public LineTrack createLineTrack() {
        LineTrack lineTrack = new LineTrack(this.getId());
        this.addValuesTrack(lineTrack);
        return lineTrack;
    }
}
