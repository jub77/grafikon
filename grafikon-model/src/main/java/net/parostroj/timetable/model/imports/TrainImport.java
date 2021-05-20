package net.parostroj.timetable.model.imports;

import java.util.Collections;
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

    private static final Logger log = LoggerFactory.getLogger(TrainImport.class);

    public TrainImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId importedObject) {
        // check class
        if (!(importedObject instanceof Train)) {
            // skip other objects
            return null;
        }
        Train importedTrain = (Train)importedObject;

        // check if the train already exist
        Train checkedTrain = this.getTrain(importedTrain);
        if (checkedTrain != null) {
            String message = "train already exists";
            this.addError(importedTrain, message);
            log.debug("{}: {}", message, checkedTrain);
            return null;
        }
        // create a new train
        TrainType trainType = this.getTrainType(importedTrain.getType());
        if (trainType == null) {
            String message = "train type missing: " + importedTrain.getType();
            this.addError(importedTrain, message);
            log.debug(message);
            return null;
        }
        Train train = getDiagram().getPartFactory().createTrain(this.getId(importedTrain));
        train.setNumber(importedTrain.getNumber());
        train.getAttributes().add(this.importAttributes(importedTrain.getAttributes(),
                Train.ATTR_NEXT_JOINED_TRAIN, Train.ATTR_PREVIOUS_JOINED_TRAIN));
        train.setDescription(importedTrain.getDescription());
        train.setType(trainType);
        train.setTopSpeed(importedTrain.getTopSpeed());

        TrainIntervalsBuilder builder = new TrainIntervalsBuilder(train, importedTrain.getStartTime());
        // create route (new)
        List<Triplet<NetSegment<?>, Track, TimeInterval>> route = createNewRoute(importedTrain);
        if (route.isEmpty()) {
            String message = "error creating route for train";
            this.addError(importedTrain, message);
            log.debug("{}: {}", message, importedTrain);
            return null;
        }
        for (Triplet<NetSegment<?>, Track, TimeInterval> seg : route) {
            if (seg.first instanceof Node) {
                // node
                Node node = (Node)seg.first;
                builder.addNode(this.getId(seg.third), node, (NodeTrack)seg.second, seg.third.getLength(), seg.third.getAttributes());
            } else {
                // line
                Line line = (Line)seg.first;
                builder.addLine(this.getId(seg.third), line, (LineTrack)seg.second, seg.third.getSpeedLimit(), seg.third.getAddedTime(), seg.third.getAttributes());
            }
        }
        builder.finish();
        train.setTimeBefore(importedTrain.getTimeBefore());
        train.setTimeAfter(importedTrain.getTimeAfter());

        this.getDiagram().getTrains().add(train);
        this.addImportedObject(train);
        log.trace("Successfully imported train: {}", train);
        return train;
    }

    private List<Triplet<NetSegment<?>, Track, TimeInterval>> createNewRoute(Train train) {
        Node previousNode = null;
        Track previousLineTrack = null;
        TimeInterval previousLineInterval = null;
        List<Triplet<NetSegment<?>, Track, TimeInterval>> segments = new LinkedList<>();
        for (TimeInterval interval : train.getTimeIntervalList()) {
            if (interval.isNodeOwner()) {
                Node node = this.getNode(interval.getOwnerAsNode());
                Track nodeTrack = node != null ? this.getTrack(node, interval.getTrack()) : null;
                if (node == null || nodeTrack == null) {
                    log.trace("Cannot find node or track: {}", interval.getOwnerAsNode());
                    return Collections.emptyList();
                }
                if (previousNode != null) {
                    // add line
                    Line line = this.getDiagram().getNet().getLine(previousNode, node);
                    Track lineTrack = line != null ? this.getTrack(line, previousLineTrack) : null;
                    if (line == null || lineTrack == null) {
                        log.trace("Cannot find line or track: {}, {}", previousNode, node);
                        return Collections.emptyList();
                    }
                    segments.add(new Triplet<>(line, lineTrack, previousLineInterval));
                }
                // add node
                segments.add(new Triplet<>(node, nodeTrack, interval));
                previousNode = node;
            } else {
                previousLineTrack = interval.getTrack();
                previousLineInterval = interval;
            }
        }
        return segments;
    }
}
