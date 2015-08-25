package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    private final AttributesWrapper attributesWrapper;

    /**
     * Constructor.
     *
     * @param id id
     */
    public Track(String id) {
        this.id = id;
        this.intervalList = new TimeIntervalList();
        this.attributesWrapper = new AttributesWrapper(
                (attrs, change) -> fireAttributeChanged(change.getName(), change.getOldValue(), change.getNewValue()));
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
        this.fireAttributeChanged(ATTR_NUMBER, oldNumber, number);
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
        return attributesWrapper.getAttributes();
    }

    public void setAttributes(Attributes attributes) {
        this.attributesWrapper.setAttributes(attributes);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributesWrapper.getAttributes().get(key, clazz);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributesWrapper.getAttributes().remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributesWrapper.getAttributes().set(key, value);
    }

    abstract void fireAttributeChanged(String attributeName, Object oldValue, Object newValue);

    @Override
    public Iterator<TimeInterval> iterator() {
        return intervalList.iterator();
    }
}
