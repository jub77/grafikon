package net.parostroj.timetable.gui.dialogs;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainIntervalsBuilder;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports trains from one diagram to another.
 *
 * @author jub
 */
public class TrainImport extends Import {

    private static final Logger LOG = LoggerFactory.getLogger(TrainImport.class.getName());

    public TrainImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected void importObjectImpl(Object importedObject) {
        // check class
        if (!(importedObject instanceof Train)) {
            // skip other objects
            return;
        }
        Train importedTrain = (Train)importedObject;

        // check if the train already exist
        Train checkedTrain = this.getTrain(importedTrain);
        if (checkedTrain != null) {
            String message = "Train already exists: " + checkedTrain;
            this.addError(importedTrain, message);
            LOG.trace(message);
            return;
        }
        // create a new train
        TrainType trainType = this.getTrainType(importedTrain.getType());
        if (trainType == null) {
            String message = "Train type missing: " + importedTrain.getType();
            this.addError(importedTrain, message);
            LOG.trace(message);
            return;
        }
        Train train = getDiagram().createTrain(this.getId(importedTrain));
        train.setNumber(importedTrain.getNumber());
        train.setType(trainType);
        train.setAttributes(importedTrain.getAttributes());
        train.setDescription(importedTrain.getDescription());
        train.setTopSpeed(importedTrain.getTopSpeed());

        TrainIntervalsBuilder builder = new TrainIntervalsBuilder(this.getDiagram(), train, importedTrain.getStartTime());
        // create route (new)
        List<Triplet<RouteSegment, Track, TimeInterval>> route = createNewRoute(importedTrain);
        if (route == null) {
            String message = "Error creating route for train: " + importedTrain;
            this.addError(importedTrain, message);
            LOG.trace(message);
            return;
        }
        for (Triplet<RouteSegment, Track, TimeInterval> seg : route) {
            if (seg.first instanceof Node) {
                // node
                Node node = (Node)seg.first;
                builder.addNode(this.getId(seg.third), node, (NodeTrack)seg.second, seg.third.getLength(), seg.third.getAttributes());
            } else {
                // line
                Line line = (Line)seg.first;
                builder.addLine(this.getId(seg.third), line, (LineTrack)seg.second, seg.third.getSpeed(), seg.third.getAttributes());
            }
        }
        builder.finish();
        train.setTimeBefore(importedTrain.getTimeBefore());
        train.setTimeAfter(importedTrain.getTimeAfter());

        this.getDiagram().addTrain(train);
        this.addImportedObject(train);
        LOG.trace("Successfully imported train: " + train);
    }

    private List<Triplet<RouteSegment, Track, TimeInterval>> createNewRoute(Train train) {
        Node previousNode = null;
        Track previousLineTrack = null;
        TimeInterval previousLineInterval = null;
        List<Triplet<RouteSegment, Track, TimeInterval>> segments = new LinkedList<Triplet<RouteSegment, Track, TimeInterval>>();
        for (TimeInterval interval : train.getTimeIntervalList()) {
            if (interval.isNodeOwner()) {
                Node node = this.getNode(interval.getOwnerAsNode());
                Track nodeTrack = node != null ? this.getTrack(node, interval.getTrack()) : null;
                if (node == null || nodeTrack == null) {
                    LOG.trace("Cannot find node or track: " + interval.getOwnerAsNode());
                    return null;
                }
                if (previousNode != null) {
                    // add line
                    Line line = this.getDiagram().getNet().getLine(previousNode, node);
                    Track lineTrack = line != null ? this.getTrack(line, previousLineTrack) : null;
                    if (line == null || lineTrack == null) {
                        LOG.trace("Cannot find line or track: " + previousNode + ", " + node);
                        return null;
                    }
                    segments.add(new Triplet<RouteSegment, Track, TimeInterval>(line, lineTrack, previousLineInterval));
                }
                // add node
                segments.add(new Triplet<RouteSegment, Track, TimeInterval>(node, nodeTrack, interval));
                previousNode = node;
            } else {
                previousLineTrack = interval.getTrack();
                previousLineInterval = interval;
            }
        }
        return segments;
    }
}
