package net.parostroj.timetable.actions;

import java.util.Collection;
import java.util.List;

import net.parostroj.timetable.model.Node;

import com.google.common.base.Predicate;

/**
 * Node sort - for output compatibility. It uses internally {@link ElementSort}.
 *
 * @author jub
 */
public class NodeSort {

    public enum Type {
        ASC
    };

    public NodeSort(Type type) {
    }

    public List<Node> sort(Collection<Node> nodes, Predicate<Node> filter) {
        return new ElementSort<Node>(new NodeComparator(), filter).sort(nodes);
    }
}
