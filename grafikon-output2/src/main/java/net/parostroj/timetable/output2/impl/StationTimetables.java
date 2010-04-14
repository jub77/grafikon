package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.parostroj.timetable.output2.impl.StationTimetable;

/**
 * Station timetables.
 *
 * @author jub
 */
@XmlRootElement(name="stations")
public class StationTimetables {

    private List<StationTimetable> stationTimetables;

    public StationTimetables() {
    }

    public StationTimetables(List<StationTimetable> stationTimetables) {
        this.stationTimetables = stationTimetables;
    }

    @XmlElement(name="station")
    public List<StationTimetable> getStationTimetables() {
        return stationTimetables;
    }

    public void setStationTimetables(List<StationTimetable> stationTimetables) {
        this.stationTimetables = stationTimetables;
    }
}
