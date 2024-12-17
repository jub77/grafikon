package net.parostroj.timetable.model.changes;

import java.util.Calendar;
import java.util.List;

/**
 * Changes tracker interface.
 *
 * @author jub
 */
public interface ChangesTracker {

    void addListener(ChangesTrackerListener listener);

    void removeAllListeners();

    void removeListener(ChangesTrackerListener listener);

    List<String> getVersions();

    List<DiagramChangeSet> getChangeSets();

    DiagramChangeSet getChangeSet(String version);

    DiagramChangeSet addVersion(String version, String author, Calendar date);

    DiagramChangeSet updateCurrentChangeSet(String version, String author, Calendar date);

    String getCurrentVersion();

    DiagramChangeSet getCurrentChangeSet();

    DiagramChangeSet setLastAsCurrent();

    void addChange(DiagramChange change);

    DiagramChangeSet removeCurrentChangeSet(boolean delete);

    void setTrackingEnabled(boolean enabled);

    boolean isTrackingEnabled();

    void clear();
}
