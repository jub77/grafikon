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
        changes.add(change);
        return Collections.singletonList(new Pair<DiagramChange, Boolean>(change,Boolean.TRUE));
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
