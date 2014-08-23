package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.*;

/**
 * Validator for changes in Node.
 *
 * @author jub
 */
public class NodeValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public NodeValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event instanceof NodeEvent && event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(Node.ATTR_REGION_START)) {
            Node node = (Node) event.getSource();
            return checkNodeControl(node);
        } else if (event instanceof TrainDiagramEvent && event.getType() == GTEventType.NODE_ADDED) {
            Node node = (Node) ((TrainDiagramEvent) event).getObject();
            return checkNodeControl(node);
        }
        return false;
    }

    private boolean checkNodeControl(Node node) {
        if (node.getAttributes().getBool(Node.ATTR_REGION_START)) {
            Region region = node.getAttribute(Node.ATTR_REGION, Region.class);
            // look through all nodes for another region start in the same region
            for (Node tn : diagram.getNet().getNodes()) {
                if (tn != node && tn.getAttribute(Node.ATTR_REGION, Region.class) == region) {
                    // ensure that there is no region start
                    tn.getAttributes().setBool(Node.ATTR_REGION_START, false);
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
