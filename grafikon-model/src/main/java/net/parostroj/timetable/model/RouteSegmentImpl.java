package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.collect.Iterators;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Observable;

/**
 * Common implementation of route segment.
 *
 * @author jub
 */
abstract class RouteSegmentImpl<T extends Track> implements RouteSegment<T>, Observable {

    /** Id of an object. */
    private final String id;
    /** List of tracks. */
    protected final List<T> tracks;

    protected final ListenerSupport listenerSupport;

    public RouteSegmentImpl(String id) {
        this.id = id;
        this.tracks = new LinkedList<>();
        this.listenerSupport = new ListenerSupport();
    }

    @Override
    public Iterator<TimeInterval> iterator() {
        return Iterators.concat(Iterators.transform(tracks.iterator(), Track::iterator));
    }

    @Override
    public void addTimeInterval(TimeInterval interval) {
        interval.getTrack().addTimeInterval(interval);
        fireTimeIntervalEvent(interval, Event.Type.ADDED);
    }

    @Override
    public void removeTimeInterval(TimeInterval interval) {
        interval.getTrack().removeTimeInterval(interval);
        fireTimeIntervalEvent(interval, Event.Type.REMOVED);
    }

    @Override
    public void updateTimeInterval(TimeInterval interval) {
        T track = this.getTrackForInterval(interval);
        if (track == null) {
            throw new IllegalStateException("Segment doesn't contain interval.");
        }
        track.removeTimeInterval(interval);
        interval.getTrack().addTimeInterval(interval);
        fireTimeIntervalEvent(interval, Event.Type.MOVED);

    }

    public void addTrack(T track) {
        track.changeCallback = this::fireTrackAttributeChanged;
        tracks.add(track);
        this.fireTrackEvent(track, Event.Type.ADDED, null, null);
    }

    public void addTrack(T track, int position) {
        track.changeCallback = this::fireTrackAttributeChanged;
        tracks.add(position, track);
        this.fireTrackEvent(track, Event.Type.ADDED, null, null);
    }

    public void removeTrack(T track) {
        track.changeCallback = this::fireTrackAttributeChanged;
        tracks.remove(track);
        this.fireTrackEvent(track, Event.Type.REMOVED, null, null);
    }

    public void moveTrack(int fromIndex, int toIndex) {
        T track = tracks.remove(fromIndex);
        tracks.add(toIndex, track);
        this.fireTrackEvent(track, Event.Type.MOVED, fromIndex, toIndex);
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

    private void fireTimeIntervalEvent(TimeInterval interval, Event.Type eventType) {
        this.listenerSupport.fireEvent(new Event(this, eventType, interval));
    }

    private void fireTrackAttributeChanged(Track track, AttributeChange change) {
        this.listenerSupport.fireEvent(new Event(this, track, change));
    }

    private void fireTrackEvent(Track track, Event.Type eventType, Integer from, Integer to) {
        if (from == null && to == null) {
            this.listenerSupport.fireEvent(new Event(this, eventType, track));
        } else {
            this.listenerSupport.fireEvent(new Event(this, eventType, track, ListData.createData(from, to)));
        }
    }

    @Override
    public void addListener(Listener listener) {
        this.listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listenerSupport.removeListener(listener);
    }
}
