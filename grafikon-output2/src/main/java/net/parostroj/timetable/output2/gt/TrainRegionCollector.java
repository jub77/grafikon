package net.parostroj.timetable.output2.gt;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.*;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.EventProcessing;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.visitors.AbstractEventVisitor;

import com.google.common.collect.Iterables;
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

    public TrainRegionCollector() {
        regions = LinkedListMultimap.create();
        collected = false;
        modifiedTrains = new HashSet<>();
    }

    @Override
    public void clear() {
        regions.clear();
        collected = false;
        modifiedTrains = new HashSet<>();
    }

    @Override
    public void addRegion(TimeInterval interval,Shape shape) {
        // add shape
        regions.put(interval.getTrain(), new Pair<>(shape, interval));
    }

    public void finishCollecting() {
        this.collected = true;
        modifiedTrains = new HashSet<>();
    }

    public boolean isCollecting(Train train) {
        return (!collected || modifiedTrains.contains(train));
    }

    private void deleteTrain(Train train) {
        regions.removeAll(train);
    }

    private void newTrain(Train train) {
        modifiedTrains.add(train);
    }

    private void modifiedTrain(Train train) {
        regions.removeAll(train);
        modifiedTrains.add(train);
    }

    @Override
    public List<TimeInterval> getItemsForPoint(int x, int y, int radius) {
        Rectangle2D cursor = new Rectangle2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        LinkedList<TimeInterval> list = new LinkedList<>();
        for (Train train : regions.keySet()) {
            for (Pair<Shape, TimeInterval> pair : regions.get(train)) {
                if (pair.first.intersects(cursor) && !list.contains(pair.second)) {
                    TimeInterval interval = pair.second;
                    if (interval.isNodeOwner()) {
                        list.addFirst(interval);
                    } else {
                        list.add(interval);
                    }
                }
            }
        }
        return list;
    }

    public boolean containsTrain(Train train) {
        return regions.containsKey(train);
    }

    @Override
    public Rectangle getRectangleForItems(List<TimeInterval> items) {
        Iterable<Train> trains = Iterables.transform(items, SelectorUtils.createToTrainFunction()::apply);
        Iterable<Train> uniqueTrains = Iterables.filter(trains, SelectorUtils.createUniqueTrainFilter()::test);

        Rectangle result = null;
        for (Train train : uniqueTrains) {
            Collection<Pair<Shape, TimeInterval>> shapes = regions.get(train);
            if (shapes != null) {
                for (Pair<Shape, TimeInterval> pair : shapes) {
                    Shape shape = pair.first;
                    Rectangle bounds = shape.getBounds();
                    if (result == null) {
                        result = bounds;
                    } else {
                        result = result.union(bounds);
                    }
                }
            }
        }
        return result;
    }

    public Collection<Pair<Shape, TimeInterval>> getRegionsForTrain(Train train) {
        return regions.get(train);
    }

    @Override
    public void processEvent(Event event) {
        AbstractEventVisitor visitor = new AbstractEventVisitor() {
            @Override
            public void visitDiagramEvent(Event event) {
                switch (event.getType()) {
                    case ADDED:
                        if (event.getObject() instanceof Train) {
                            newTrain((Train) event.getObject());
                        }
                        break;
                    case REMOVED:
                        if (event.getObject() instanceof Train) {
                            deleteTrain((Train) event.getObject());
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void visitTrainEvent(Event event) {
                switch (event.getType()) {
                    case SPECIAL:
                        modifiedTrain((Train) event.getSource());
                        break;
                    case ATTRIBUTE:
                        if (event.getAttributeChange().checkName(Train.ATTR_MANAGED_FREIGHT, Train.ATTR_TECHNOLOGICAL_BEFORE, Train.ATTR_TECHNOLOGICAL_AFTER)) {
                            modifiedTrain((Train) event.getSource());
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        EventProcessing.visit(event, visitor);
    }
}
