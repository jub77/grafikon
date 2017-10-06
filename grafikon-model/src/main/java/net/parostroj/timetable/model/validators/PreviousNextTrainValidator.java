package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;
import net.parostroj.timetable.model.events.SpecialTrainTimeIntervalList;
import net.parostroj.timetable.utils.TimeUtil;

/**
 * Validator for joined trains feature. It keeps aligned tracks of joined train,
 * moves joined trains and adjusts technological time after for joined trains.
 *
 * @author jub
 */
public class PreviousNextTrainValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(Event event) {
        checkRemovedTrain(event);
        if (event.getSource() instanceof Train) {
            Train currentTrain = (Train) event.getSource();
            checkTrainAttributes(event, currentTrain);
            checkTrainIntervals(event, currentTrain);
            return true;
        }
        return false;
    }

    private void checkRemovedTrain(Event event) {
        if (event.getSource() instanceof TrainDiagram
                && event.getType() == Type.REMOVED && event.getObject() instanceof Train) {
            Train currentTrain = (Train) event.getObject();
            if (currentTrain.getPreviousJoinedTrain() != null) {
                currentTrain.getPreviousJoinedTrain().setNextJoinedTrain(null);
            }
            if (currentTrain.getNextJoinedTrain() != null) {
                currentTrain.setNextJoinedTrain(null);
            }
        }
    }

    private void checkTrainAttributes(Event event, Train currentTrain) {
        if (event.getType() == Type.ATTRIBUTE) {
            switch (event.getAttributeChange().getName()) {
                case Train.ATTR_NEXT_JOINED_TRAIN:
                    checkAndUpdateTrackAndTimeAfter(
                            currentTrain,
                            (Train) event.getAttributeChange().getNewValue());
                    break;
                case Train.ATTR_TECHNOLOGICAL_AFTER:
                    if (currentTrain.getNextJoinedTrain() != null) {
                        checkAndUpdateNextTrainStart(currentTrain);
                        checkAndUpdateTimeAfter(currentTrain);
                    }
                    break;
                case Train.ATTR_TECHNOLOGICAL_BEFORE:
                    if (currentTrain.getPreviousJoinedTrain() != null) {
                        currentTrain.setTimeBefore(0);
                    }
                    break;
                default:
                    // nothing
            }
        }
    }

    private void checkTrainIntervals(Event event, Train currentTrain) {
        if (event.getType() == Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
            SpecialTrainTimeIntervalList list = (SpecialTrainTimeIntervalList) event.getData();
            if (list.getType() == SpecialTrainTimeIntervalList.Type.TRACK) {
                int changed = list.getChanged();
                if (currentTrain.getPreviousJoinedTrain() != null && changed == 0) {
                    TimeInterval source = currentTrain.getFirstInterval();
                    TimeInterval dest = currentTrain.getPreviousJoinedTrain().getLastInterval();
                    checkAndUpdateTrack(source, dest);
                } else if (currentTrain.getNextJoinedTrain() != null && changed == currentTrain.getTimeIntervalList().size() - 1) {
                    TimeInterval source = currentTrain.getLastInterval();
                    TimeInterval dest = currentTrain.getNextJoinedTrain().getFirstInterval();
                    checkAndUpdateTrack(source, dest);
                }
            }
            if (currentTrain.getNextJoinedTrain() != null) {
                checkAndUpdateNextTrainStart(currentTrain);
                checkAndUpdateTimeAfter(currentTrain);
            }
            if (currentTrain.getPreviousJoinedTrain() != null) {
                checkAndUpdateTimeAfter(currentTrain.getPreviousJoinedTrain());
            }
        }
    }

    private void checkAndUpdateTrackAndTimeAfter(Train currentTrain, Train newNextTrain) {
        if (newNextTrain != null) {
            TimeInterval source = currentTrain.getLastInterval();
            TimeInterval dest = newNextTrain.getFirstInterval();
            checkAndUpdateTrack(source, dest);
            checkAndUpdateTimeAfter(currentTrain);
        } else {
            currentTrain.setTimeAfter(0);
        }
    }

    private void checkAndUpdateTrack(TimeInterval source, TimeInterval dest) {
        Track track = source.getTrack();
        if (track != dest.getTrack()) {
            dest.getTrain().changeNodeTrack(dest, (NodeTrack) track);
        }
    }

    private void checkAndUpdateTimeAfter(Train currentTrain) {
        Train nextTrain = currentTrain.getNextJoinedTrain();
        if (nextTrain != null) {
            int startTime = currentTrain.getEndTime();
            int endTime = nextTrain.getStartTime();
            int length = TimeUtil.difference(startTime, endTime);
            if (length != currentTrain.getTimeAfter()) {
                currentTrain.setTimeAfter(length);
            }
            if (nextTrain.getTimeBefore() != 0) {
                nextTrain.setTimeBefore(0);
            }
        }
    }

    private void checkAndUpdateNextTrainStart(Train currentTrain) {
        Train nextTrain = currentTrain.getNextJoinedTrain();
        if (nextTrain != null) {
            int nextStart = TimeUtil.normalizeTime(currentTrain.getEndTime() + currentTrain.getTimeAfter());
            if (nextStart != nextTrain.getStartTime()) {
                nextTrain.move(nextStart);
            }
        }
    }
}
