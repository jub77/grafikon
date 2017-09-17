package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.ObjectWithId;

/**
 * Dummy object as a replacement for object which is not loaded yet. It will
 * be replaced by real object with the same id at some point of loading
 * the diagram.
 *
 * @author jub
 */
public class DelayedObjectWithId implements ObjectWithId {

    private final String id;

    public DelayedObjectWithId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
