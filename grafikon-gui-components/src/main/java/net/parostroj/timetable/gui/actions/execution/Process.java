package net.parostroj.timetable.gui.actions.execution;

import net.parostroj.timetable.model.ObjectWithId;

/**
 * Action for import.
 *
 * @author jub
 */
public interface Process<T extends ObjectWithId> {
    void apply(T item);
}
