package net.parostroj.timetable.gui.components;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.*;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.Pair;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Class for collecting regions of the trains from graphical timetable.
 *
 * @author jub
 */
public class TrainRegionCollector extends RegionCollector<TimeInterval> {

    private final Multimap<Train, Pair<Shape, TimeInterval>> regions;

    private boolean collected;

    private Set<Train> modifiedTrains;

    // sensitivity radius
    private final int radius;

    public TrainRegionCollector(int radius) {
        regions = LinkedListMultimap.create();
        collected = false;
        modifiedTrains = new HashSet<Train>();
        this.radius = radius;
    }

    @Override
    public void clear() {
        regions.clear();
        collected = false;
        modifiedTrains = new HashSet<Train>();
    }

    @Override
    public void addRegion(TimeInterval interval,Shape shape) {
        // add shape
        regions.put(interval.getTrain(), new Pair<Shape, TimeInterval>(shape, interval));
    }

    public void finishCollecting() {
        this.collected = true;
        modifiedTrains = new HashSet<Train>();
    }

    public boolean isCollecting(Train train) {
        return (!collected || modifiedTrains.contains(train));
    }

    public void deleteTrain(Train train) {
        regions.removeAll(train);
    }

    public void newTrain(Train train) {
        modifiedTrains.add(train);
    }

    public void modifiedTrain(Train train) {
        regions.removeAll(train);
        modifiedTrains.add(train);
    }

    @Override
    public List<TimeInterval> getItemsForPoint(int x, int y) {
        Rectangle2D cursor = new Rectangle2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        List<TimeInterval> list = new LinkedList<TimeInterval>();
        for (Train train : regions.keySet()) {
            for (Pair<Shape, TimeInterval> pair : regions.get(train)) {
                if (pair.first.intersects(cursor) && !list.contains(pair.second))
                    list.add(pair.second);
            }
        }
        return list;
    }
}
