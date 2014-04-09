package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.NetEvent;

/**
 * Region remove callback.
 *
 * @author jub
 */
public class RegionRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public RegionRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event instanceof NetEvent && event.getType() == GTEventType.REGION_REMOVED) {
            Region region = (Region) ((NetEvent) event).getObject();
            // remove region from all nodes where it appears
            for (Node node : diagram.getNet().getNodes()) {
                if (region == node.getAttributes().get(Node.ATTR_REGION, Region.class)) {
                    node.getAttributes().remove(Node.ATTR_REGION);
                }
            }
            return true;
        }
        return false;
    }
}
