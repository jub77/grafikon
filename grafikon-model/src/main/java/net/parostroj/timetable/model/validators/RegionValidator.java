package net.parostroj.timetable.model.validators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

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
                Region region = (Region) event.getAttributeChange().getNewValue();
                if (region != null) {
                    this.removeRegionFromNet(region);
                    return true;
                }
                // in case the region cease to exist as super-region - do not allow color center on non-super-region
                Region oldRegion = (Region) event.getAttributeChange().getOldValue();
                if (oldRegion != null) {
                    checkColorCenterWhenNotSuperRegion(oldRegion);
                }
            }
            // in case a region changes color center attribute
            if (event.getAttributeChange().checkName(Region.ATTR_COLOR_CENTER)) {
                Region region = (Region) event.getObject();
                checkColorCenterWhenNotSuperRegion(region);
                checkFreightColorMapWithColorCenter(region);
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

    private void checkColorCenterWhenNotSuperRegion(Region region) {
        if (region.isColorCenter() && !region.isSuperRegion()) {
            region.setColorCenter(false);
        }
    }

    private void checkFreightColorMapWithColorCenter(Region region) {
        if (!region.isColorCenter() && !region.getFreightColorMap().isEmpty()) {
            region.setFreightColorMap(null);
        }
    }
}
