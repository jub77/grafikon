package net.parostroj.timetable.model.validators;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * Validator for changes in Line
 *
 * @author jub
 */
public class LineValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof Line && event.getType() == Type.ATTRIBUTE && event.getAttributeChange().checkName(Line.ATTR_LENGTH, Line.ATTR_SPEED)) {
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
        } else if (event.getSource() instanceof Line && event.getType() == Type.OBJECT_ATTRIBUTE
                && event.getObject() instanceof Track
                && event.getAttributeChange().checkName(Track.ATTR_FROM_STRAIGHT, Track.ATTR_TO_STRAIGHT)) {
            for (TimeInterval i : ImmutableList.copyOf(((Track) event.getObject()).getTimeIntervalList())) {
                i.getTrain().recalculate();
            }
            return true;
        }
        return false;
    }

}
