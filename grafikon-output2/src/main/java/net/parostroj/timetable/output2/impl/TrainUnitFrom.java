package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Train unit from.
 *
 * @author jub
 */
@XmlType(propOrder = {"cycleName", "cycleDescription"})
public class TrainUnitFrom {

    private String cycleName;
    private String cycleDescription;

    public TrainUnitFrom() {
    }

    public TrainUnitFrom(String cycleName, String cycleDescription) {
        this.cycleName = cycleName;
        this.cycleDescription = cycleDescription;
    }

    public String getCycleDescription() {
        return cycleDescription;
    }

    public void setCycleDescription(String cycleDescription) {
        this.cycleDescription = cycleDescription;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }
}
