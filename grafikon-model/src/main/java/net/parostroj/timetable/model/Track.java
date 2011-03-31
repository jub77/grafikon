package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.List;

/**
 * Track in the station.
 *
 * @author jub
 */
public abstract class Track implements AttributesHolder, ObjectWithId {
    /** ID. */
    private final String id;
    /** Track number. */
    private String number;
    /** Interval list. */
    private TimeIntervalList intervalList;
    /** Attributes. */
    private Attributes attributes;

    /**
     * Constructor.
     * 
     * @param id id
     */
    public Track(String id) {
        this.id = id;
        intervalList = new TimeIntervalList();
        attributes = new Attributes();
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
        this.fireAttributeChanged("number", oldNumber, number);
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

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Object removeAttribute(String key) {
        Object returnValue = attributes.remove(key);
        this.fireAttributeChanged(key, returnValue, null);
        return returnValue;
    }

    @Override
    public void setAttribute(String key, Object value) {
        Object oldValue = attributes.get(key);
        attributes.set(key, value);
        this.fireAttributeChanged(key, oldValue, value);
    }
    
    abstract void fireAttributeChanged(String attributeName, Object oldValue, Object newValue);
}
