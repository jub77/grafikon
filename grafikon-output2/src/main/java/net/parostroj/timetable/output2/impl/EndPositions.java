package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Root element for end positions.
 *
 * @author jub
 */
@XmlRootElement
public class EndPositions {

    private List<Position> enginesPositions;
    private List<Position> trainUnitsPositions;

    public EndPositions() {
    }

    @XmlElementWrapper
    @XmlElement(name = "position")
    public List<Position> getEnginesPositions() {
        return enginesPositions;
    }

    public void setEnginesPositions(List<Position> enginesPositions) {
        this.enginesPositions = enginesPositions;
    }

    @XmlElementWrapper
    @XmlElement(name = "position")
    public List<Position> getTrainUnitsPositions() {
        return trainUnitsPositions;
    }

    public void setTrainUnitsPositions(List<Position> trainUnitsPositions) {
        this.trainUnitsPositions = trainUnitsPositions;
    }
}
