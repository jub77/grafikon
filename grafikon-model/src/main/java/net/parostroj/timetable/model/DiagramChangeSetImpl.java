package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.changes.DiagramChange;
import net.parostroj.timetable.model.changes.DiagramChangeSet;
import net.parostroj.timetable.utils.Pair;

/**
 * Set of changes with specified name.
 *
 * @author jub
 */
class DiagramChangeSetImpl implements DiagramChangeSet {

    private String version;
    private List<DiagramChange> changes;

    public DiagramChangeSetImpl(String version) {
        this.version = version;
        this.changes = new LinkedList<DiagramChange>();
    }

    List<Pair<DiagramChange,Boolean>> addChange(DiagramChange change) {
        // add change
        // TODO implementation of logic missing
        // look for existing changes
        List<DiagramChange> existing = getChangesForId(change.getObjectId());
        List<Pair<DiagramChange, Boolean>> returning = new LinkedList<Pair<DiagramChange, Boolean>>();
        boolean shouldAdd = true;
        if (existing != null) {
            for (DiagramChange ex : existing) {
                // logic
                shouldAdd &= shouldAdd(change.getAction(), ex.getAction());
                if (shouldRemove(change.getAction(), ex.getAction())) {
                    changes.remove(ex);
                    returning.add(new Pair<DiagramChange, Boolean>(ex, Boolean.FALSE));
                }
            }
        }
        if (shouldAdd) {
            changes.add(change);
            returning.add(new Pair<DiagramChange, Boolean>(change, Boolean.TRUE));
        }
        return returning;
    }

    private boolean shouldAdd(DiagramChange.Action added, DiagramChange.Action existing) {
        switch (added) {
            case ADDED:
                return true;
            case MODIFIED:
                return existing != DiagramChange.Action.ADDED && existing != DiagramChange.Action.MODIFIED;
            case MOVED:
                return existing != DiagramChange.Action.ADDED && existing != DiagramChange.Action.MOVED;
            case REMOVED:
                return true;
        }
        return false;
    }

    private boolean shouldRemove(DiagramChange.Action added, DiagramChange.Action existing) {
        switch (existing) {
            case ADDED:
                return added == DiagramChange.Action.REMOVED;
            case MODIFIED:
                return added == DiagramChange.Action.REMOVED;
            case MOVED:
                return added == DiagramChange.Action.REMOVED;
        }
        return false;
    }

    private List<DiagramChange> getChangesForId(String id) {
        List<DiagramChange> result = null;
        for (DiagramChange change : changes) {
            if (change.getObjectId().equals(id)) {
                if (result == null)
                    result = new LinkedList<DiagramChange>();
                result.add(change);
            }
        }
        return result;
    }

    @Override
    public List<DiagramChange> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    @Override
    public String getVersion() {
        return this.version;
    }
}
