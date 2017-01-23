package net.parostroj.timetable.model.validators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * Region validator.
 *
 * @author jub
 */
public class RegionValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public RegionValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof Net && event.getType() == Type.REMOVED
                && event.getObject() instanceof Region) {
            this.removeRegionFromNet((Region) event.getObject());
            return true;
        }
        if (event.getType() == Type.OBJECT_ATTRIBUTE && event.getObject() instanceof Region) {
            if (event.getAttributeChange().checkName(Region.ATTR_SUPER_REGION)) {
                // in case a region changes to super-region
                Region superRegion = (Region) event.getAttributeChange().getNewValue();
                if (superRegion != null) {
                    this.removeRegionFromNet(superRegion);
                }
                Region region = (Region) event.getObject();
                Region firstColorCenter = getColorCenterRegion(region);
                if (firstColorCenter != null) {
                    checkRegionsInHierarchyForDuplicateColorCenter(firstColorCenter);
                }
            }
            // in case a region changes color center attribute
            if (event.getAttributeChange().checkName(Region.ATTR_COLOR_CENTER)) {
                Region region = (Region) event.getObject();
                checkFreightColorMapWithColorCenter(region);
                checkOtherFreightColorMapsWhenColorCenterDeactivates(region);
                checkRegionsInHierarchyForDuplicateColorCenter(region);
            }
            // in case freight color map is set
            if (event.getAttributeChange().checkName(Region.ATTR_FREIGHT_COLOR_MAP)) {
                Region region = (Region) event.getObject();
                checkFreightColorMapWithColorCenter(region);
            }
        }
        // in case region is used - cannot be used as super-region
        if (event.getSource() instanceof Node && event.getType() == Type.ATTRIBUTE &&
                event.getAttributeChange().checkName(Node.ATTR_REGIONS)) {
            for (Region region : ((Node) event.getSource()).getRegions()) {
                if (region.isSuperRegion()) this.cancelSuperRegion(region);
            }
            checkCommonSuperRegion((Node) event.getSource());
        }
        return false;
    }

    // checks that all regions have the same super-region
    private void checkCommonSuperRegion(Node node) {
        Set<Region> regions = node.getRegions();
        Iterator<Region> iterator = regions.iterator();
        List<Region> toBeRemovedRegions = null;
        if (regions.size() > 1 && iterator.hasNext()) {
            Region commonSuperRegion = iterator.next().getSuperRegion();
            while (iterator.hasNext()) {
                Region region = iterator.next();
                Region superRegion = region.getSuperRegion();
                if (superRegion != commonSuperRegion) {
                    if (toBeRemovedRegions == null) toBeRemovedRegions = new ArrayList<>();
                    toBeRemovedRegions.add(region);
                }
            }
        }
        if (toBeRemovedRegions != null) {
            for (Region removedRegion : toBeRemovedRegions) {
                regions = removeRegion(regions, removedRegion);
            }
            node.setAttribute(Node.ATTR_REGIONS, regions);
        }
    }

    private void cancelSuperRegion(Region region) {
        for (Region subR : ImmutableList.copyOf(region.getSubRegions())) {
            subR.setSuperRegion(null);
        }
    }

    private void removeRegionFromNet(Region region) {
        // remove region from all nodes where it appears
        for (Node node : diagram.getNet().getNodes()) {
            if (node.getRegions().contains(region)) {
                node.setRemoveAttribute(Node.ATTR_REGIONS, this.removeRegion(node.getRegions(), region));
                node.setRemoveAttribute(Node.ATTR_CENTER_OF_REGIONS,
                        this.removeRegion(node.getCenterRegions(), region));
            }
        }
    }

    private Set<Region> removeRegion(Set<Region> regions, Region toBeRemoved) {
    	Set<Region> newList = new HashSet<>(regions);
    	newList.remove(toBeRemoved);
    	return newList.isEmpty() ? null : newList;
    }

    private void checkFreightColorMapWithColorCenter(Region region) {
        if (!region.isColorCenter() && !region.getFreightColorMap().isEmpty()) {
            region.setFreightColorMap(null);
        }
    }

    private void checkOtherFreightColorMapsWhenColorCenterDeactivates(Region region) {
        // remove existing color mapping to this region
        if (!region.isColorCenter()) {
            diagram.getNet().getRegions().stream().filter(r -> r != region).filter(r -> r.isColorCenter())
                    .forEach(r -> {
                        Map<FreightColor, Region> colorMap = r.getFreightColorMap();
                        if (colorMap.values().contains(region)) {
                            r.setFreightColorMap(colorMap.entrySet().stream().filter(e -> e.getValue() != region)
                                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
                        }
                    });
        }
    }

    private void checkRegionsInHierarchyForDuplicateColorCenter(Region region) {
        if (region.isColorCenter()) {
            // check all sub regions...
            removeColorCenterSubRegions(region);
            removeColorCenterSuperRegions(region);
            // check freight colors
            region.getAllNodes().stream()
                    .filter(n -> !n.getFreightColors().isEmpty())
                    .forEach(n -> NodeValidator.checkAllowedFreightColors(n));
        }
    }

    private void removeColorCenterSuperRegions(Region region) {
        Region current = region.getSuperRegion();
        while (current != null) {
            if (current.isColorCenter()) {
                current.setColorCenter(false);
            }
            current = current.getSuperRegion();
        }
    }

    private void removeColorCenterSubRegions(Region region) {
        for (Region subRegion : region.getSubRegions()) {
            if (subRegion.isColorCenter()) {
                subRegion.setColorCenter(false);
            }
            removeColorCenterSubRegions(subRegion);
        }
    }

    private Region getColorCenterRegion(Region start) {
        while (start != null && !start.isColorCenter()) {
            start = start.getSuperRegion();
        }
        return start;
    }
}
