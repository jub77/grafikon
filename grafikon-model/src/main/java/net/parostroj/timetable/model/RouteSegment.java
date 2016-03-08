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
public interface RouteSegment<T extends Track> extends ObjectWithId, Iterable<TimeInterval> {

    public Line asLine();

    public Node asNode();

    public boolean isLine();

    public boolean isNode();

    public void addTimeInterval(TimeInterval interval);

    public void removeTimeInterval(TimeInterval interval);

    public void updateTimeInterval(TimeInterval interval);

    public List<T> getTracks();

    public boolean isEmpty();

    @Override
    public String getId();

    public T getTrackById(String id);

    public T getTrackByNumber(String name);
}
