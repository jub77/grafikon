package net.parostroj.timetable.model.freight;

import java.util.List;

import net.parostroj.timetable.model.Node;

public interface NodeConnectionNodes extends NodeConnection {

    List<Node> getNodes();

    default Node getFirstNode() {
        List<Node> nodes = getNodes();
        return nodes.isEmpty() ? null : nodes.get(0);
    }
}
