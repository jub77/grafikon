package net.parostroj.timetable.model.freight;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.parostroj.timetable.model.Node;

public interface NodeConnectionNodes extends NodeConnection {

    List<Node> getIntermediateNodes();

    default Node getFirstIntermediateNode() {
        List<Node> nodes = getIntermediateNodes();
        return nodes.isEmpty() ? null : nodes.getFirst();
    }

    default Node getNextNode() {
        Node firstIntermediateNode = getFirstIntermediateNode();
        return firstIntermediateNode != null ? firstIntermediateNode : getTo();
    }

    default List<Node> getPath() {
        return ImmutableList.<Node>builder().add(getFrom()).addAll(getIntermediateNodes()).add(getTo()).build();
    }
}
