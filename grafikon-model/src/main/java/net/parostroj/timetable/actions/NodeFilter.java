package net.parostroj.timetable.actions;

import net.parostroj.timetable.model.Node;

/**
 * Node filter interface.
 *
 * @author jub
 */
public interface NodeFilter {

    public boolean check(Node node);
}
