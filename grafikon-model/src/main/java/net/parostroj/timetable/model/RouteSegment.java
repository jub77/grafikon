/*
 * RouteSegment.java
 *
 * Created on 31.8.2007, 9:59:23
 */
package net.parostroj.timetable.model;

import java.util.List;

/**
 * Route segment.
 *
 * @author jub
 */
public interface RouteSegment<T extends Track> extends ObjectWithId, Iterable<TimeInterval>, AttributesHolder {

    void addTimeInterval(TimeInterval interval);

    void removeTimeInterval(TimeInterval interval);

    void updateTimeInterval(TimeInterval interval);

    List<T> getTracks();

    boolean isEmpty();

    @Override
    String getId();

    T getTrackById(String id);
}
