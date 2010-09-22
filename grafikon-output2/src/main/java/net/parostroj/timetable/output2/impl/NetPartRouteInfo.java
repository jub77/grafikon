package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Info about route - name and stations.
 *
 * @author jub
 */
@XmlType(propOrder={"name", "stations"})
public class NetPartRouteInfo {

    private String name;
    private List<String> stations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="station")
    public List<String> getStations() {
        if (stations == null)
            stations = new LinkedList<String>();
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }
}
