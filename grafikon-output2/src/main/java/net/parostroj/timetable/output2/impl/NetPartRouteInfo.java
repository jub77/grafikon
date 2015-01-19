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
@XmlType(propOrder={"name", "segments"})
public class NetPartRouteInfo {

    private String name;
    private List<RouteSegmentInfo> segments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="segment")
    public List<RouteSegmentInfo> getSegments() {
        if (segments == null)
            segments = new LinkedList<RouteSegmentInfo>();
        return segments;
    }

    public void setSegments(List<RouteSegmentInfo> segments) {
        this.segments = segments;
    }
}
