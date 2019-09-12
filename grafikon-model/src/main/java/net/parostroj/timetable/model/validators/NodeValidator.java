package net.parostroj.timetable.model.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            if (event.getAttributeChange().checkName(Node.ATTR_CENTER_OF_REGIONS)) {
                Node node = (Node) event.getSource();
                return checkNodeControl(node);
            } else if (event.getAttributeChange().checkName(Node.ATTR_LENGTH, Node.ATTR_NOT_STRAIGHT_SPEED, Node.ATTR_SPEED)) {
                List<TimeInterval> intervals = Lists.newArrayList((Node) event.getSource());
                for (TimeInterval i : intervals) {
                    i.getTrain().recalculate();
                }
            } else if (event.getAttributeChange().checkName(Node.ATTR_FREIGHT_COLORS, Node.ATTR_REGIONS)) {
                checkAllowedFreightColors((Node) event.getSource());
            }
        } else if (event.getSource() instanceof TrainDiagram && event.getType() == Type.ADDED && event.getObject() instanceof Node) {
            Node node = (Node) event.getObject();
            return checkNodeControl(node);
        } else if (event.getSource() instanceof TrainDiagram && event.getType() == Type.ATTRIBUTE
                && event.getAttributeChange().checkName(TrainDiagram.ATTR_CHANGE_DIRECTION_STOP)) {
            diagram.getTrains().forEach(Train::recalculate);
        }

        return false;
    }
    private boolean inCheckNodeControl;

    private boolean checkNodeControl(Node node) {
        boolean applied = false;
        if (!inCheckNodeControl) {
            try {
                inCheckNodeControl = true;
                applied = checkImpl(node);
            } finally {
                inCheckNodeControl = false;
            }
        }
        return applied;
    }

    private boolean checkImpl(Node node) {
        boolean applied = false;
        Set<Region> regions = node.getCenterRegions();
        if (!regions.isEmpty()) {
            for (Region region : regions) {
                checkOnlyOneRegionCenter(node, region);
            }
            applied = true;
        } else {
            for (TimeInterval interval : node) {
                interval.removeAttribute(TimeInterval.ATTR_NO_REGION_CENTER_TRANSFER);
            }
        }
        return applied;
    }

    private void checkOnlyOneRegionCenter(Node node, Region region) {
        // look through all nodes for another region start in
        // the same region
        for (Node tn : diagram.getNet().getNodes()) {
            if (tn != node && tn.getCenterRegions().contains(region)) {
                // ensure that there is no region start
                Set<Region> newSet = new HashSet<>(tn.getCenterRegions());
                newSet.remove(region);
                tn.setRemoveAttribute(Node.ATTR_CENTER_OF_REGIONS, newSet.isEmpty() ? null : newSet);
            }
        }
    }

    static void checkAllowedFreightColors(Node node) {
        if (node.getFreightColors().isEmpty()) {
            return;
        }
        // get all nodes with the same color center
        Optional<Region> center = node.getRegionHierarchy().findInRegions(Region::isFreightColorRegion);
        if (center.isPresent()) {
            Set<FreightColor> colors = node.getFreightColors();
            center.get().getAllNodes().stream().filter(n -> n != node && !n.getFreightColors().isEmpty()).forEach(n -> {
                Set<FreightColor> nColors = new HashSet<>(n.getFreightColors());
                if (nColors.removeAll(colors)) {
                    n.setFreightColors(nColors);
                }
            });
        }
    }
}
