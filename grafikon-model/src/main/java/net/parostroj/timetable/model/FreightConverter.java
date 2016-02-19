package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.actions.TextList;

/**
 * Conversion to string representation.
 *
 * @author jub
 */
public class FreightConverter {

	public List<FreightDst> convertFreightDst(TimeInterval from, List<FreightDst> list) {
		List<Region> regions = from.getOwnerAsNode().getRegions();
        Region region = regions.isEmpty() ? null : regions.get(0);
        return convertFreightDst(from.getTrain(), region, list);
    }

    public List<FreightDst> convertFreightDst(Train train, Region region, List<FreightDst> list) {
        List<FreightDst> result = new LinkedList<FreightDst>();
        Set<Region> used = new HashSet<Region>();
        Set<FreightDst> visited = new HashSet<FreightDst>();
        for (FreightDst dst : list) {
            if (!dst.isNode()) {
                throw new IllegalArgumentException("Only node destinations allowed.");
            }
            if (visited.contains(dst)) {
                continue;
            }
            Region nRegion = dst.getRegion();
            boolean regionMatch = nRegion != null && nRegion != region;
            boolean otherTrain = train != dst.getTrain();
            if (regionMatch && otherTrain && dst.getNode().getType() != NodeType.STATION_HIDDEN) {
                addRegion(result, used, nRegion);
            } else {
                visited.add(dst);
                result.add(dst);
            }
            if (!dst.getNode().getCenterRegions().isEmpty() && nRegion != null) {
                addRegion(result, used, nRegion);
            }
        }
        return result;
    }

    public String freightDstListToString(Collection<FreightDst> list) {
        StringBuilder builder = new StringBuilder();
        TextList output = new TextList(builder, ",");
        output.addItems(list);
        output.finish();
        return builder.toString();
    }

    private void addRegion(List<FreightDst> result, Set<Region> used, Region nRegion) {
        if (!used.contains(nRegion)) {
            result.add(new FreightDst(nRegion, null));
            used.add(nRegion);
        }
    }
}
