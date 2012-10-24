package net.parostroj.timetable.actions;

import java.text.Collator;
import java.util.*;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.model.Node;

/**
 * Sorting of nodes.
 *
 * @author jub
 */
public class NodeSort {

    public enum Type {ASC, DESC; }

    private final Type type;

    public NodeSort(Type type) {
        this.type = type;
    }

    /**
     * sorts list of nodes.
     *
     * @param nodes nodes
     * @return sorted list
     */
    public List<Node> sort(Collection<Node> nodes) {
        List<Node> newNodes = new ArrayList<Node>(nodes);
        this.sortInternal(newNodes);
        return newNodes;
    }

    /**
     * sorts list of nodes and removes signal nodes.
     *
     * @param nodes collection of nodes
     * @return sorted collections
     */
    public List<Node> sort(Collection<Node> nodes, Filter<Node> filter) {
        List<Node> newNodes = new ArrayList<Node>(nodes.size());
        for (Node node : nodes) {
            if (filter.is(node))
                newNodes.add(node);
        }
        this.sortInternal(newNodes);
        return newNodes;
    }

    private void sortInternal(List<Node> nodes) {
        Comparator<Node> comparator = null;
        switch (type) {
            case ASC:
                comparator = new Comparator<Node>() {
                    private final Collator c = Collator.getInstance();
                    @Override
                    public int compare(Node o1, Node o2) {
                        return c.compare(o1.getName(), o2.getName());
                    }
                };
                break;
            case DESC:
                comparator = new Comparator<Node>() {
                    private final Collator c = Collator.getInstance();
                    @Override
                    public int compare(Node o1, Node o2) {
                        return c.compare(o2.getName(), o1.getName());
                    }
                };
                break;
        }
        Collections.sort(nodes, comparator);
    }
}