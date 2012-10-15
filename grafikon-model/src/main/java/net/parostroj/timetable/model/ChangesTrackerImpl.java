package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.parostroj.timetable.model.changes.*;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListenerWithNested;
import net.parostroj.timetable.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tracks changes in the model in order to provides a list
 * of changed objects for network communication.
 *
 * @author jub
 */
class ChangesTrackerImpl implements TrainDiagramListenerWithNested, ChangesTracker {

    private static final Logger LOG = LoggerFactory.getLogger(ChangesTrackerImpl.class.getName());

    private List<DiagramChangeSetImpl> sets;
    private TrackedCheckVisitor trackedVisitor;
    private TransformVisitor transformVisitor;
    private Set<ChangesTrackerListener> listeners;
    private DiagramChangeSetImpl _currentChangeSet;
    private boolean enabled;

    ChangesTrackerImpl() {
        sets = new LinkedList<DiagramChangeSetImpl>();
        trackedVisitor = new TrackedCheckVisitor();
        transformVisitor = new TransformVisitor();
        listeners = new HashSet<ChangesTrackerListener>();
    }

    private void receiveEvent(GTEvent<?> event) {
        if (!enabled)
            return;

        event = event.getLastNestedEvent();
        // check if the event belongs to tracked events
        if (!isTracked(event))
            return;

        // add to changes
        event.accept(transformVisitor);
        DiagramChange change = transformVisitor.getChange();
        this.addChange(change);
    }

    @Override
    public void addChange(DiagramChange change) {
        if (_currentChangeSet == null) {
            String message = "Current change set is empty.";
            LOG.warn(message);
            throw new IllegalStateException(message);
        }
        List<Pair<DiagramChange, ChangesTrackerEvent.Type>> arChanges = _currentChangeSet.addChange(change);
        for (Pair<DiagramChange, ChangesTrackerEvent.Type> pair : arChanges) {
            this.fireEvent(new ChangesTrackerEvent(pair.second, _currentChangeSet, pair.first));
        }
    }

    private void fireEvent(ChangesTrackerEvent event) {
        // inform listeners
        for (ChangesTrackerListener l : this.listeners) {
            l.trackerChanged(event);
        }
    }

    private boolean isTracked(GTEvent<?> event) {
        event.accept(trackedVisitor);
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
    public void trainDiagramChangedNested(TrainDiagramEvent event) {
        this.receiveEvent(event);
    }

    @Override
    public void trainDiagramChanged(TrainDiagramEvent event) {
        this.receiveEvent(event);
    }

    @Override
    public List<String> getVersions() {
        List<String> versions = new ArrayList<String>(sets.size());
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
        if (version == null)
            version = createVersion();
        _currentChangeSet = new DiagramChangeSetImpl(version, author, date);
        this.sets.add(_currentChangeSet);
        this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.SET_ADDED, _currentChangeSet));
        return _currentChangeSet;
    }

    @Override
    public String getCurrentVersion() {
        return _currentChangeSet != null ? _currentChangeSet.getVersion() : null;
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
        return _currentChangeSet;
    }

    @Override
    public DiagramChangeSet removeCurrentChangeSet(boolean delete) {
        DiagramChangeSet returned = _currentChangeSet;
        if (_currentChangeSet != null && delete) {
            sets.remove(_currentChangeSet);
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.SET_REMOVED, _currentChangeSet));
        }
        if (_currentChangeSet != null) {
            _currentChangeSet = null;
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.CURRENT_SET_CHANGED));
        }
        return returned;
    }

    private String createVersion() {
        // get last version
        String lastVersion = null;
        List<String> versions = getVersions();
        if (versions.size() > 0)
            lastVersion = versions.get(versions.size() - 1);
        if (lastVersion == null)
            lastVersion = "0";
        try {
            int ilv = Integer.parseInt(lastVersion);
            ilv++;
            lastVersion = Integer.toString(ilv);
        } catch (NumberFormatException e) {
            LOG.warn("Cannot parse version string: {} ({})", lastVersion, e.getMessage());
            lastVersion = "1";
        }
        return lastVersion;
    }

    @Override
    public DiagramChangeSet setLastAsCurrent() {
        if (sets.isEmpty()) {
            _currentChangeSet = null;
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.CURRENT_SET_CHANGED));
        } else {
            _currentChangeSet = sets.get(sets.size() - 1);
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.CURRENT_SET_CHANGED, _currentChangeSet));
        }
        return _currentChangeSet;
    }

    @Override
    public List<DiagramChangeSet> getChangeSets() {
        return Collections.<DiagramChangeSet>unmodifiableList(sets);
    }

    @Override
    public DiagramChangeSet updateCurrentChangeSet(String version, String author, Calendar date) {
        if (_currentChangeSet != null) {
            _currentChangeSet.setAuthor(author);
            _currentChangeSet.setDate(date);
            _currentChangeSet.setVersion(version);
            this.fireEvent(new ChangesTrackerEvent(ChangesTrackerEvent.Type.SET_MODIFIED, _currentChangeSet));
        }
        return _currentChangeSet;
    }
}
