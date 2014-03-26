package net.parostroj.timetable.model.validators;

import java.util.HashSet;
import java.util.Set;

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
                for (TimeInterval interval : track.getTimeIntervalList()) {
                    trains.add(interval.getTrain());
                }
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
