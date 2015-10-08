package net.parostroj.timetable.model.validators;

import java.util.List;

import com.google.common.collect.Lists;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

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
    public boolean validate(Event event) {
        if (event.getSource() instanceof Node && event.getType() == Type.ATTRIBUTE) {
            if (event.getAttributeChange().checkName(Node.ATTR_REGION_START)) {
                Node node = (Node) event.getSource();
                return checkNodeControl(node);
            } else if (event.getAttributeChange().checkName(Node.ATTR_LENGTH, Node.ATTR_NOT_STRAIGHT_SPEED, Node.ATTR_SPEED)) {
                List<TimeInterval> intervals = Lists.newArrayList((Node) event.getSource());
                for (TimeInterval i : intervals) {
                    i.getTrain().recalculate();
                }
            }
        } else if (event.getSource() instanceof TrainDiagram && event.getType() == Type.ADDED && event.getObject() instanceof Node) {
            Node node = (Node) event.getObject();
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
