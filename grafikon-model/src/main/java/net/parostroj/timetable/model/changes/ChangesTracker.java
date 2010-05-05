package net.parostroj.timetable.model.changes;

import java.util.List;

/**
 * Changes tracker interface.
 *
 * @author jub
 */
public interface ChangesTracker {

    public void addListener(ChangesTrackerListener listener);

    public void removeAllListeners();

    public void removeListener(ChangesTrackerListener listener);

    public List<String> getVersions();

    public List<DiagramChangeSet> getChangeSets();

    public DiagramChangeSet getChangeSet(String version);

    public DiagramChangeSet addVersion(String version);

    public String getCurrentVersion();

    public DiagramChangeSet getCurrentChangeSet();

    public DiagramChangeSet setLastAsCurrent();

    public void removeCurrentChangeSet(boolean delete);

    public void setTrackingEnabled(boolean enabled);

    public boolean isTrackingEnabled();
}
