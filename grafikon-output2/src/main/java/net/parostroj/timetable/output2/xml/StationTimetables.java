package net.parostroj.timetable.output2.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import net.parostroj.timetable.output2.impl.StationTimetable;

/**
 * Station timetables.
 *
 * @author jub
 */
@XmlRootElement
public class StationTimetables {

    private List<StationTimetable> stationTimetable;

    public StationTimetables() {
    }

    public StationTimetables(List<StationTimetable> stationTimetable) {
        this.stationTimetable = stationTimetable;
    }

    public List<StationTimetable> getStationTimetable() {
        return stationTimetable;
    }

    public void setStationTimetable(List<StationTimetable> stationTimetable) {
        this.stationTimetable = stationTimetable;
    }
}
