package net.parostroj.timetable.model.validators;

import java.util.ArrayList;
import java.util.List;

import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.Event;
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
            	if (node.getRegions().contains(region)) {
            		node.setRemoveAttribute(Node.ATTR_REGIONS, this.removeRegion(node.getRegions(), region));
            		node.setRemoveAttribute(Node.ATTR_CENTER_OF_REGIONS, this.removeRegion(node.getCenterRegions(), region));
            	}
            }
            return true;
        }
        return false;
    }

    private List<Region> removeRegion(List<Region> regions, Region toBeRemoved) {
    	List<Region> newList = new ArrayList<>(regions);
    	newList.remove(toBeRemoved);
    	return newList.isEmpty() ? null : newList;
    }
}
