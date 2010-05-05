package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.parostroj.timetable.model.changes.ChangesTracker;
import net.parostroj.timetable.model.changes.ChangesTrackerListener;
import net.parostroj.timetable.model.changes.DiagramChange;
import net.parostroj.timetable.model.changes.DiagramChangeSet;
import net.parostroj.timetable.model.changes.TrackedCheckVisitor;
import net.parostroj.timetable.model.changes.TransformVisitor;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListenerWithNested;

/**
 * This class tracks changes in the model in order to provides a list
 * of changed objects for network communication.
 *
 * @author jub
 */
class ChangesTrackerImpl implements TrainDiagramListenerWithNested, ChangesTracker {

    private static final Logger LOG = Logger.getLogger(ChangesTrackerImpl.class.getName());

    private List<DiagramChangeSet> changes;
    private TrackedCheckVisitor trackedVisitor;
    private TransformVisitor transformVisitor;
    private Set<ChangesTrackerListener> listeners;
    private String currentVersion;
    private DiagramChangeSet _currentChangeSet;
    private boolean enabled;

    ChangesTrackerImpl() {
        changes = new LinkedList<DiagramChangeSet>();
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
        if (_currentChangeSet != null)
            _currentChangeSet.addChange(change);

        // inform listeners
        for (ChangesTrackerListener l : this.listeners) {
            l.changeReceived(change);
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
        List<String> versions = new ArrayList<String>(changes.size());
        for (DiagramChangeSet set : changes)
            versions.add(set.getVersion());
        return versions;
    }

    @Override
    public DiagramChangeSet getChangeSet(String version) {
        for (DiagramChangeSet set : changes) {
            if (set.getVersion().equals(version))
                return set;
        }
        return null;
    }

    @Override
    public DiagramChangeSet addVersion(String version) {
        if (version == null)
            version = createVersion();
        _currentChangeSet = new DiagramChangeSet(version);
        this.changes.add(_currentChangeSet);
        return _currentChangeSet;
    }

    @Override
    public String getCurrentVersion() {
        return this.currentVersion;
    }

    @Override
    public void setTrackingEnabled(boolean enabled) {
        this.enabled = enabled;
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
    public void removeCurrentChangeSet(boolean delete) {
        if (_currentChangeSet != null && delete) {
            changes.remove(_currentChangeSet);
        }
        _currentChangeSet = null;
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
            LOG.log(Level.WARNING, "Cannot parse version string: {0} ({1})", new Object[]{lastVersion, e.getMessage()});
            lastVersion = "1";
        }
        return lastVersion;
    }

    @Override
    public DiagramChangeSet setLastAsCurrent() {
        if (changes.isEmpty())
            return null;
        else
            return changes.get(changes.size() - 1);
    }
}
