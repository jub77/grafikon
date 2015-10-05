package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.collect.Iterators;

import net.parostroj.timetable.model.events.*;

/**
 * Common implementation of route segment.
 *
 * @author jub
 */
abstract class RouteSegmentImpl<T extends Track> implements RouteSegment {

    /** Id of an object. */
    private final String id;
    /** List of tracks. */
    protected final List<T> tracks;

    public RouteSegmentImpl(String id) {
        this.id = id;
        this.tracks = new LinkedList<T>();
    }

    @Override
    public Iterator<TimeInterval> iterator() {
        return Iterators.concat(Iterators.transform(tracks.iterator(), track -> track.iterator()));
    }

    @Override
    public void addTimeInterval(TimeInterval interval) {
        interval.getTrack().addTimeInterval(interval);
        fireTimeIntervalEvent(interval, GTEventType.TIME_INTERVAL_ADDED);
    }

    @Override
    public void removeTimeInterval(TimeInterval interval) {
        interval.getTrack().removeTimeInterval(interval);
        fireTimeIntervalEvent(interval, GTEventType.TIME_INTERVAL_REMOVED);
    }

    @Override
    public void updateTimeInterval(TimeInterval interval) {
        T track = this.getTrackForInterval(interval);
        if (track == null) {
            throw new IllegalStateException("Segment doesn't contain interval.");
        }
        track.removeTimeInterval(interval);
        interval.getTrack().addTimeInterval(interval);
        fireTimeIntervalEvent(interval, GTEventType.TIME_INTERVAL_UPDATED);

    }

    public void addTrack(T track) {
        track.changeCallback = this::fireTrackAttributeChanged;
        tracks.add(track);
        this.fireTrackEvent(track, GTEventType.TRACK_ADDED, null, null);
    }

    public void addTrack(T track, int position) {
        track.changeCallback = this::fireTrackAttributeChanged;
        tracks.add(position, track);
        this.fireTrackEvent(track, GTEventType.TRACK_ADDED, null, null);
    }

    public void removeTrack(T track) {
        track.changeCallback = this::fireTrackAttributeChanged;
        tracks.remove(track);
        this.fireTrackEvent(track, GTEventType.TRACK_REMOVED, null, null);
    }

    public void moveTrack(int fromIndex, int toIndex) {
        T track = tracks.remove(fromIndex);
        tracks.add(toIndex, track);
        this.fireTrackEvent(track, GTEventType.TRACK_MOVED, fromIndex, toIndex);
    }

    @Override
    public List<T> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    @Override
    public boolean isEmpty() {
        for (T track : tracks) {
            if (!track.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public T getTrackById(String id) {
        for (T track : tracks) {
            if (track.getId().equals(id)) {
                return track;
            }
        }
        return null;
    }

    @Override
    public T getTrackByNumber(String number) {
        for (T track : tracks) {
            if (track.getNumber().equals(number)) {
                return track;
            }
        }
        return null;
    }

    private T getTrackForInterval(TimeInterval interval) {
        for (T track : tracks) {
            if (track.getTimeIntervalList().contains(interval))
                return track;
        }
        return null;
    }

    abstract protected void fireTimeIntervalEvent(TimeInterval interval, GTEventType eventType);

    abstract protected void fireTrackAttributeChanged(Track track, AttributeChange attributeChange);

    abstract protected void fireTrackEvent(Track track, GTEventType eventType, Integer from, Integer to);
}
