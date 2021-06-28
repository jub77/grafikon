package net.parostroj.timetable.output2.impl;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Root element for start positions.
 *
 * @author jub
 */
@XmlRootElement
@XmlType(propOrder = { "enginesPositions", "trainUnitsPositions", "customCycles" })
public class StartPositions {

    private List<Position> enginesPositions;
    private List<Position> trainUnitsPositions;
    private List<Cycles> customCycles;

    @XmlElementWrapper(name = "engines")
    @XmlElement(name = "position")
    public List<Position> getEnginesPositions() {
        return enginesPositions;
    }

    public void setEnginesPositions(List<Position> enginesPositions) {
        this.enginesPositions = enginesPositions;
    }

    @XmlElementWrapper(name = "trainUnits")
    @XmlElement(name = "position")
    public List<Position> getTrainUnitsPositions() {
        return trainUnitsPositions;
    }

    public void setTrainUnitsPositions(List<Position> trainUnitsPositions) {
        this.trainUnitsPositions = trainUnitsPositions;
    }

    @XmlElementWrapper(name = "custom")
    @XmlElement(name = "circulation")
    public List<Cycles> getCustomCycles() {
        return customCycles;
    }

    public void setCustomCycles(List<Cycles> customCycles) {
        this.customCycles = customCycles;
    }
}
