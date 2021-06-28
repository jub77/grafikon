package net.parostroj.timetable.output2.impl;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Root element for end positions.
 *
 * @author jub
 */
@XmlRootElement
public class EndPositions {

    private List<Position> enginesPositions;
    private List<Position> trainUnitsPositions;

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
