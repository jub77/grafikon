package net.parostroj.timetable.actions;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;

/**
 * Destination for cargo.
 *
 * @author jub
 */
public class FreightDst {

    private final Node node;
    private final Region region;

    public FreightDst(Region region) {
        this.region = region;
        this.node = null;
    }

    public FreightDst(Node node) {
        this.region = null;
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return node == null ? region.getName() : node.getAbbr();
    }
}
