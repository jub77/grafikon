package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.model.changes.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tracks changes in the model in order to provide a list
 * of changed objects for network communication.
 *
 * @author jub
 */
class ChangesTrackerImpl implements Listener, ChangesTracker {

    private static final Logger log = LoggerFactory.getLogger(ChangesTrackerImpl.class);

    private final List<DiagramChangeSetImpl> sets;
    private final TrackedCheckVisitor trackedVisitor;
    private final TransformVisitor transformVisitor;
    private final Set<ChangesTrackerListener> listeners;
    private DiagramChangeSetImpl currentChangeSet;
    private boolean enabled;

    ChangesTrackerImpl() {
        sets = new LinkedList<>();
        trackedVisitor = new TrackedCheckVisitor();
        transformVisitor = new TransformVisitor();
        listeners = new HashSet<>();
    }

    private void receiveEvent(Event event) {
        if (!enabled) {
            return;
        }

        // check if the event belongs to tracked events
        if (!isTracked(event)) {
            return;
        }

        // add to changes
        EventProcessing.visit(event, transformVisitor);
        DiagramChange change = transformVisitor.getChange();
        this.addChange(change);
    }

    @Override
    public void addChange(DiagramChange change) {
        if (currentChangeSet == null) {
            String message = "Current change set is empty.";
            log.warn(message);
            throw new IllegalStateException(message);
        }
        List<Pair<DiagramChange, ChangesTrackerEvent.Type>> arChanges = currentChangeSet.addChange(change);
        for (Pair<DiagramChange, ChangesTrackerEvent.Type> pair : arChanges) {
            this.fireEvent(new ChangesTrackerEvent(pair.second, currentChangeSet, pair.first));
        }
    }

    private void fireEvent(ChangesTrackerEvent event) {
        // inform listeners
        for (ChangesTrackerListener l : this.listeners) {
            l.trackerChanged(event);
        }
    }

    private boolean isTracked(Event event) {
        EventProcessing.visit(event, trackedVisitor);
        return trackedVisitor.isTracked();
    }

    @Override
    public void addListener(ChangesTrackerListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(ChangesTrackerListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public void changed(Event event) {
        this.receiveEvent(event);
    }

    @Override
    public List<String> getVersions() {
        List<String> versions = new ArrayList<>(sets.size());
        for (DiagramChangeSet set : sets)
            versions.add(set.getVersion());
        return versions;
    }

    @Override
    public DiagramChangeSet getChangeSet(String version) {
        for (DiagramChangeSet set : sets) {
            if (set.getVersion().equals(version))
                return set;
        }
        return null;
    }

    @Override
    public DiagramChangeSet addVersion(String version, String author, Calendar date) {
        String aVersion = version != null ? version : createVersion();
        currentChangeSet = new DiagramChangeSetImpl(aVersion, author, date);
        this.sets.add(currentChangeSet);
        this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.SET_ADDED, currentChangeSet));
        return currentChangeSet;
    }

    @Override
    public String getCurrentVersion() {
        return currentChangeSet != null ? currentChangeSet.getVersion() : null;
    }

    @Override
    public void setTrackingEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            this.fireEvent(new ChangesTrackerEvent(enabled ? ChangesTrackerEvent.Type.TRACKING_ENABLED : ChangesTrackerEvent.Type.TRACKING_DISABLED));
        }
    }

    @Override
    public boolean isTrackingEnabled() {
        return this.enabled;
    }

    @Override
    public DiagramChangeSet getCurrentChangeSet() {
        return currentChangeSet;
    }

    @Override
    public DiagramChangeSet removeCurrentChangeSet(boolean delete) {
        DiagramChangeSet returned = currentChangeSet;
        if (currentChangeSet != null && delete) {
            sets.remove(currentChangeSet);
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.SET_REMOVED, currentChangeSet));
        }
        if (currentChangeSet != null) {
            currentChangeSet = null;
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.CURRENT_SET_CHANGED));
        }
        return returned;
    }

    private String createVersion() {
        // get last version
        String lastVersion = null;
        List<String> versions = getVersions();
        if (!versions.isEmpty()) {
            lastVersion = versions.getLast();
        }
        if (lastVersion == null) {
            lastVersion = "0";
        }
        try {
            int ilv = Integer.parseInt(lastVersion);
            ilv++;
            lastVersion = Integer.toString(ilv);
        } catch (NumberFormatException e) {
            log.warn("Cannot parse version string: {} ({})", lastVersion, e.getMessage());
            lastVersion = "1";
        }
        return lastVersion;
    }

    @Override
    public DiagramChangeSet setLastAsCurrent() {
        if (sets.isEmpty()) {
            currentChangeSet = null;
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.CURRENT_SET_CHANGED));
        } else {
            currentChangeSet = sets.getLast();
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.CURRENT_SET_CHANGED, currentChangeSet));
        }
        return currentChangeSet;
    }

    @Override
    public List<DiagramChangeSet> getChangeSets() {
        return Collections.unmodifiableList(sets);
    }

    @Override
    public DiagramChangeSet updateCurrentChangeSet(String version, String author, Calendar date) {
        if (currentChangeSet != null) {
            currentChangeSet.setAuthor(author);
            currentChangeSet.setDate(date);
            currentChangeSet.setVersion(version);
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.SET_MODIFIED, currentChangeSet));
        }
        return currentChangeSet;
    }

    @Override
    public void clear() {
        while (!sets.isEmpty()) {
            this.removeCurrentChangeSet(true);
            this.setLastAsCurrent();
        }
    }
}
