package net.parostroj.timetable.model.ls.impl4.filters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d22 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 22, 0)) <= 0) {
            for (Node node : diagram.getNet().getNodes()) {
                node.getAttributes().setSkipListeners(true);
                try {
                    Collection<Region> regions = node.getAttributeAsCollection(Node.ATTR_REGIONS, Region.class);
                    if (regions != null && !(regions instanceof Set)) {
                        node.setAttribute(Node.ATTR_REGIONS, new HashSet<>(regions));
                    }
                    Collection<Region> centerRegions = node.getAttributeAsCollection(Node.ATTR_CENTER_OF_REGIONS, Region.class);
                    if (centerRegions != null && !(centerRegions instanceof Set)) {
                        node.setAttribute(Node.ATTR_CENTER_OF_REGIONS, new HashSet<>(centerRegions));
                    }
                    Collection<FreightColor> freightColors = node.getAttributeAsCollection(Node.ATTR_FREIGHT_COLORS, FreightColor.class);
                    if (freightColors != null && !(freightColors instanceof Set)) {
                        node.setAttribute(Node.ATTR_FREIGHT_COLORS, new HashSet<>(freightColors));
                    }
                } finally {
                    node.getAttributes().setSkipListeners(false);
                }
            }
        }
    }
}
