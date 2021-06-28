package net.parostroj.timetable.output2.impl;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * Part of route info.
 *
 * @author jub
 */
public class RouteInfoPart {

    String part;
    Boolean highlighted;

    public RouteInfoPart() {}

    public RouteInfoPart(String part, Boolean highlighted) {
        this.part = part;
        this.highlighted = highlighted;
    }

    @XmlValue
    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    @XmlAttribute(name="emph")
    public Boolean getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }
}
