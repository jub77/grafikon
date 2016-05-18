package net.parostroj.timetable.output2.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;

/**
 * @author jub
 */
@XmlType(propOrder = {"name", "positions"})
public class Cycles {

    private LocalizedString name;
    private List<Position> positions;

    public LocalizedString getName() {
        return name;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public void setName(LocalizedString name) {
        this.name = name;
    }

    @XmlElement(name = "position")
    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
