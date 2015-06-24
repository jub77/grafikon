package net.parostroj.timetable.actions;

import java.text.Collator;
import java.util.Comparator;

import net.parostroj.timetable.model.Node;

/**
 * Comparator for nodes - it uses collator for name comparison.
 *
 * @author jub
 */
public class NodeComparator implements Comparator<Node> {

    private final Collator collator = Collator.getInstance();

    @Override
    public int compare(Node o1, Node o2) {
        return collator.compare(o1.getName(), o2.getName());
    }
}
