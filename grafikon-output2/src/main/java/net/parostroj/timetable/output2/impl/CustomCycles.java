package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Custom cycles.
 *
 * @author jub
 */
@XmlRootElement
public class CustomCycles {

    private List<CustomCycle> cycles;

    public CustomCycles() {
    }

    public CustomCycles(List<CustomCycle> cycles) {
        this.cycles = cycles;
    }

    @XmlElement(name="cycle")
    public List<CustomCycle> getCycles() {
        return cycles;
    }

    public void setCycles(List<CustomCycle> cycles) {
        this.cycles = cycles;
    }
}
