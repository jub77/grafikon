package net.parostroj.timetable.output2.impl;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Train unit cycles.
 *
 * @author jub
 */
@XmlRootElement
public class TrainUnitCycles {

    private List<TrainUnitCycle> cycles;

    public TrainUnitCycles() {
    }

    public TrainUnitCycles(List<TrainUnitCycle> cycles) {
        this.cycles = cycles;
    }

    @XmlElement(name="cycle")
    public List<TrainUnitCycle> getCycles() {
        return cycles;
    }

    public void setCycles(List<TrainUnitCycle> cycles) {
        this.cycles = cycles;
    }
}
