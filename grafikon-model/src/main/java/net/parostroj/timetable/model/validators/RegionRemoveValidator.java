package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

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
    public boolean validate(Event event) {
        if (event.getSource() instanceof Net && event.getType() == Type.REMOVED && event.getObject() instanceof Region) {
            Region region = (Region) event.getObject();
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
