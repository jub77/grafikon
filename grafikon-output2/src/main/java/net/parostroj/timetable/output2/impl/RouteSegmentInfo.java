package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * Route segment info.
 *
 * @author jub
 */
@XmlType(propOrder={"name", "type", "distance"})
public class RouteSegmentInfo {

    private String name;
    private String type;
    private Double distance;

    @XmlAttribute
    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @XmlValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
