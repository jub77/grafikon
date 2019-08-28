package net.parostroj.timetable.model.validators;

import java.util.HashSet;
import java.util.Set;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.events.Event;
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
            Set<Train> trains = new HashSet<>();
            for (TimeInterval interval : line) {
                trains.add(interval.getTrain());
            }
            // recalculate collected trains
            for (Train train : trains) {
                train.recalculate();
            }
            return true;
        }
        return false;
    }
}
