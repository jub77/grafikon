package net.parostroj.timetable.output2.impl;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
