package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.ManagedFreight;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d27 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 27, 0)) <= 0) {
            // changes for older or equals than 4.27
            for (Train train : diagram.getTrains()) {
                boolean managedFreight = train.getAttributeAsBool("managed.freight");
                if (managedFreight) {
                    train.removeAttribute("managed.freight");
                    train.computeAndSetManagedFreight(ManagedFreight.ALL);
                    for (TimeInterval interval : train.getNodeIntervals()) {
                        boolean notManaged = interval.getAttributeAsBool("not.managed.freight");
                        if (notManaged) {
                            interval.removeAttribute("not.managed.freight");
                            interval.computeAndSetFreight(false);
                        }
                    }
                }
            }
        }
    }
}
