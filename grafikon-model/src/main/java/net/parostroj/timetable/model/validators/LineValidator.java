package net.parostroj.timetable.model.validators;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.LineEvent;

/**
 * Validator for changes in Line
 *
 * @author jub
 */
public class LineValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event instanceof LineEvent && event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(Line.ATTR_LENGTH, Line.ATTR_SPEED)) {
            Line line = (Line) event.getSource();
            Set<Train> trains = new HashSet<Train>();
            for (Track track : line.getTracks()) {
                for (TimeInterval interval : ImmutableList.copyOf(track.getTimeIntervalList())) {
                    trains.add(interval.getTrain());
                }
            }
            // recalculate collected trains
            for (Train train : trains) {
                train.recalculate();
            }
            return true;
        } else if (event instanceof LineEvent && event.getType() == GTEventType.TRACK_ATTRIBUTE && event.getAttributeChange().checkName(Track.ATTR_FROM_STRAIGHT, Track.ATTR_TO_STRAIGHT)) {
            LineEvent le = (LineEvent) event;
            for (TimeInterval i : ImmutableList.copyOf(le.getTrack().getTimeIntervalList())) {
                i.getTrain().recalculate();
            }
            return true;
        }
        return false;
    }

}
