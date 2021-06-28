package net.parostroj.timetable.output2.impl;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Engine cycles.
 *
 * @author jub
 */
@XmlRootElement
public class EngineCycles {

    private List<EngineCycle> cycles;

    public EngineCycles() {
    }

    public EngineCycles(List<EngineCycle> cycles) {
        this.cycles = cycles;
    }

    @XmlElement(name="cycle")
    public List<EngineCycle> getCycles() {
        return cycles;
    }

    public void setCycles(List<EngineCycle> cycles) {
        this.cycles = cycles;
    }
}
