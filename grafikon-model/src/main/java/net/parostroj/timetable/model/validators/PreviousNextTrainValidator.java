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

public class PreviousNextTrainValidator implements TrainDiagramValidator {

    private boolean changing;

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram
                && event.getType() == Type.REMOVED && event.getObject() instanceof Train) {
            Train currentTrain = (Train) event.getObject();
            if (currentTrain.getNextJoinedTrain() != null) {
                updateNextTrain(currentTrain, currentTrain.getNextJoinedTrain(), null);
            }
            if (currentTrain.getPreviousJoinedTrain() != null) {
                updatePreviousTrain(currentTrain, currentTrain.getPreviousJoinedTrain(), null);
            }
        }
        if (event.getSource() instanceof Train) {
            Train currentTrain = (Train) event.getSource();
            if (event.getType() == Type.ATTRIBUTE) {
                switch (event.getAttributeChange().getName()) {
                    case Train.ATTR_PREVIOUS_JOINED_TRAIN:
                        updatePreviousTrain(
                                currentTrain,
                                (Train) event.getAttributeChange().getOldValue(),
                                (Train) event.getAttributeChange().getNewValue());
                        break;
                    case Train.ATTR_NEXT_JOINED_TRAIN:
                        updateNextTrain(
                                currentTrain,
                                (Train) event.getAttributeChange().getOldValue(),
                                (Train) event.getAttributeChange().getNewValue());
                        break;
                    case Train.ATTR_TECHNOLOGICAL_AFTER:
                        if (currentTrain.getNextJoinedTrain() != null) {
                            checkAndUpdateNextTrainStart(currentTrain);
                            checkAndUpdateTechnologicalAfter(currentTrain);
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
            if (event.getType() == Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
                SpecialTrainTimeIntervalList list = (SpecialTrainTimeIntervalList) event.getData();
                if (list.getType() == SpecialTrainTimeIntervalList.Type.TRACK) {
                    int changed = list.getChanged();
                    if (changed == 0 && currentTrain.getPreviousJoinedTrain() != null) {
                        TimeInterval source = currentTrain.getFirstInterval();
                        TimeInterval dest = currentTrain.getPreviousJoinedTrain().getLastInterval();
                        checkAndUpdateTrack(source, dest);
                    } else if (changed == currentTrain.getTimeIntervalList().size() - 1
                            && currentTrain.getNextJoinedTrain() != null) {
                        TimeInterval source = currentTrain.getLastInterval();
                        TimeInterval dest = currentTrain.getNextJoinedTrain().getFirstInterval();
                        checkAndUpdateTrack(source, dest);
                    }
                }
                if (currentTrain.getNextJoinedTrain() != null) {
                    checkAndUpdateNextTrainStart(currentTrain);
                    checkAndUpdateTechnologicalAfter(currentTrain);
                } else if (currentTrain.getPreviousJoinedTrain() != null) {
                    checkAndUpdateTechnologicalAfter(currentTrain.getPreviousJoinedTrain());
                }
            }
            return true;
        }
        return false;
    }

    private void updateNextTrain(Train currentTrain, Train oldNextTrain, Train newNextTrain) {
        if (changing) return;
        try {
            changing = true;
            if (oldNextTrain != null && oldNextTrain.getPreviousJoinedTrain() != null) {
                oldNextTrain.setPreviousJoinedTrain(null);
            }
            if (newNextTrain != null && newNextTrain.getPreviousJoinedTrain() != currentTrain) {
                TimeInterval source = currentTrain.getLastInterval();
                TimeInterval dest = newNextTrain.getFirstInterval();
                if (!checkNode(source, dest)) {
                    currentTrain.setNextJoinedTrain(null);
                } else {
                    checkAndUpdateTrack(source, dest);
                    if (newNextTrain.getPreviousJoinedTrain() != null) {
                        newNextTrain.getPreviousJoinedTrain().setNextJoinedTrain(null);
                    }
                    newNextTrain.setPreviousJoinedTrain(currentTrain);
                }
                checkAndUpdateTechnologicalAfter(currentTrain);
            }
            if (newNextTrain == null) {
                currentTrain.setTimeAfter(0);
            }
        } finally {
            changing = false;
        }
    }

    private void updatePreviousTrain(Train currentTrain, Train oldPrevTrain, Train newPrevTrain) {
        if (changing) return;
        try {
            changing = true;
            if (oldPrevTrain != null && oldPrevTrain.getNextJoinedTrain() != null) {
                oldPrevTrain.setNextJoinedTrain(null);
            }
            if (newPrevTrain != null && newPrevTrain.getNextJoinedTrain() != currentTrain) {
                TimeInterval source = currentTrain.getFirstInterval();
                TimeInterval dest = newPrevTrain.getLastInterval();
                if (!checkNode(source, dest)) {
                    currentTrain.setPreviousJoinedTrain(null);
                } else {
                    checkAndUpdateTrack(source, dest);
                    if (newPrevTrain.getNextJoinedTrain() != null) {
                        newPrevTrain.getNextJoinedTrain().setPreviousJoinedTrain(null);
                    }
                    newPrevTrain.setNextJoinedTrain(currentTrain);
                }
                checkAndUpdateTechnologicalAfter(newPrevTrain);
            }
            if (newPrevTrain == null) {
                oldPrevTrain.setTimeAfter(0);
            }
        } finally {
            changing = false;
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

    private void checkAndUpdateTechnologicalAfter(Train currentTrain) {
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
            int nextStart = currentTrain.getEndTime() + currentTrain.getTimeAfter();
            if (nextStart != nextTrain.getStartTime()) {
                nextTrain.move(nextStart);
            }
        }
    }
}
