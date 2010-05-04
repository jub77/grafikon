package net.parostroj.timetable.model.changes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set of changes with specified name.
 *
 * @author jub
 */
public class DiagramChangeSet {

    private String version;
    private List<DiagramChange> changes;

    public DiagramChangeSet(String version) {
        this.version = version;
        this.changes = new LinkedList<DiagramChange>();
    }

    public void addChange(DiagramChange change) {
        // add change
        // TODO implementation of logic missing
        changes.add(change);
    }

    public List<DiagramChange> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    public String getVersion() {
        return this.version;
    }
}
