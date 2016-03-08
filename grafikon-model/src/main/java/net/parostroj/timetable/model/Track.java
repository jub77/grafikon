package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.parostroj.timetable.model.events.AttributeChange;

/**
 * Track in the station.
 *
 * @author jub
 */
public abstract class Track implements AttributesHolder, ObjectWithId, TrackAttributes, Iterable<TimeInterval> {
    /** ID. */
    private final String id;
    /** Track number. */
    private String number;
    /** Interval list. */
    private final TimeIntervalList intervalList;
    /** Attributes. */
    private final Attributes attributes;

    interface ChangeCallback {
        void fireTrackAttributeChanged(Track track, AttributeChange attributeChange);
    }

    ChangeCallback changeCallback;

    /**
     * Constructor.
     *
     * @param id id
     */
    public Track(String id) {
        this.id = id;
        this.intervalList = new TimeIntervalList();
        this.attributes = new Attributes(
                (attrs, change) -> fireAttributeChanged(change));
    }

    /**
     * creates instance with specified track number.
     *
     * @param number track number
     */
    public Track(String id, String number) {
        this(id);
        this.setNumber(number);
    }

    /**
     * @return id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return track number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number track number to be set
     */
    public void setNumber(String number) {
        String oldNumber = this.number;
        this.number = number;
        this.fireAttributeChanged(new AttributeChange(ATTR_NUMBER, oldNumber, number));
    }

    /**
     * @return time interval list for this track
     */
    public List<TimeInterval> getTimeIntervalList() {
        return Collections.unmodifiableList(intervalList);
    }

    /**
     * removes time interval for specified train.
     *
     * @param interval time interval
     */
    void removeTimeInterval(TimeInterval interval) {
        intervalList.removeIntervalForRouteSegment(interval);
    }

    /**
     * adds time interval to the track.
     *
     * @param interval time interval
     */
    void addTimeInterval(TimeInterval interval) {
        interval.setTrack(this);
        intervalList.addIntervalForRouteSegment(interval);
    }

    /**
     * tests time interval (it doesn't return overlapping intervals).
     *
     * @param interval time interval
     * @return result
     */
    TimeIntervalResult testTimeInterval(TimeInterval interval) {
        return intervalList.testIntervalForRouteSegment(interval);
    }

    /**
     * tests time interval.
     *
     * @param interval time interval
     * @return result
     */
    TimeIntervalResult testTimeIntervalOI(TimeInterval interval) {
        return intervalList.testIntervalForRouteSegmentOI(interval);
    }

    @Override
    public String toString() {
        return number;
    }

    public boolean isEmpty() {
        return intervalList.isEmpty();
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    void fireAttributeChanged(AttributeChange attributeChange) {
        if (changeCallback != null) {
            changeCallback.fireTrackAttributeChanged(this, attributeChange);
        }
    }

    @Override
    public Iterator<TimeInterval> iterator() {
        return intervalList.iterator();
    }
}
