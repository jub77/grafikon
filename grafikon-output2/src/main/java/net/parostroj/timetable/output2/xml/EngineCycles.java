package net.parostroj.timetable.output2.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.parostroj.timetable.output2.impl.EngineCycle;

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
