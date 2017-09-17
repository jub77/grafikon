package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;
import net.parostroj.timetable.model.events.SpecialTrainTimeIntervalList;

public class PreviousNextTrainValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram
                && event.getType() == Type.REMOVED && event.getObject() instanceof Train) {
            Train currentTrain = (Train) event.getObject();
            updateNextTrain(currentTrain, currentTrain.getNextTrain(), null);
            updatePreviousTrain(currentTrain, currentTrain.getPreviousTrain(), null);
        }
        if (event.getSource() instanceof Train) {
            Train currentTrain = (Train) event.getSource();
            if (event.getType() == Type.ATTRIBUTE) {
                switch (event.getAttributeChange().getName()) {
                    case Train.ATTR_PREVIOUS_TRAIN:
                        updatePreviousTrain(
                                currentTrain,
                                (Train) event.getAttributeChange().getOldValue(),
                                (Train) event.getAttributeChange().getNewValue());
                        break;
                    case Train.ATTR_NEXT_TRAIN:
                        updateNextTrain(
                                currentTrain,
                                (Train) event.getAttributeChange().getOldValue(),
                                (Train) event.getAttributeChange().getNewValue());
                        break;
                    default:
                        // nothing
                }
            }
            if (event.getType() == Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
                SpecialTrainTimeIntervalList list = (SpecialTrainTimeIntervalList) event.getData();
                if (list.getType() == SpecialTrainTimeIntervalList.Type.TRACK) {
                    int changed = list.getChanged();
                    if (changed == 0 && currentTrain.getPreviousTrain() != null) {
                        TimeInterval source = currentTrain.getFirstInterval();
                        TimeInterval dest = currentTrain.getPreviousTrain().getLastInterval();
                        checkAndUpdateTrack(source, dest);
                    } else if (changed == currentTrain.getTimeIntervalList().size() - 1
                            && currentTrain.getNextTrain() != null) {
                        TimeInterval source = currentTrain.getLastInterval();
                        TimeInterval dest = currentTrain.getNextTrain().getFirstInterval();
                        checkAndUpdateTrack(source, dest);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void updateNextTrain(Train currentTrain, Train oldNextTrain, Train newNextTrain) {
        if (oldNextTrain != null && oldNextTrain.getPreviousTrain() != null) {
            oldNextTrain.setPreviousTrain(null);
        }
        if (newNextTrain != null && newNextTrain.getPreviousTrain() != currentTrain) {
            TimeInterval source = currentTrain.getLastInterval();
            TimeInterval dest = newNextTrain.getFirstInterval();
            if (!checkNode(source, dest)) {
                currentTrain.setNextTrain(null);
            } else {
                checkAndUpdateTrack(source, dest);
                newNextTrain.setPreviousTrain(currentTrain);
            }
        }
    }

    private void updatePreviousTrain(Train currentTrain, Train oldPrevTrain, Train newPrevTrain) {
        if (oldPrevTrain != null && oldPrevTrain.getNextTrain() != null) {
            oldPrevTrain.setNextTrain(null);
        }
        if (newPrevTrain != null && newPrevTrain.getNextTrain() != currentTrain) {
            TimeInterval source = currentTrain.getFirstInterval();
            TimeInterval dest = newPrevTrain.getLastInterval();
            if (!checkNode(source, dest)) {
                currentTrain.setPreviousTrain(null);
            } else {
                checkAndUpdateTrack(source, dest);
                newPrevTrain.setNextTrain(currentTrain);
            }
        }
    }

    private boolean checkNode(TimeInterval source, TimeInterval dest) {
        return source.getOwner() == dest.getOwner();
    }

    private void checkAndUpdateTrack(TimeInterval source, TimeInterval dest) {
        if (source.getOwner() != dest.getOwner()) {
            throw new IllegalStateException("Not the same node");
        }
        Track track = source.getTrack();
        if (track != dest.getTrack()) {
            dest.getTrain().changeNodeTrack(dest, (NodeTrack) track);
        }
    }
}
