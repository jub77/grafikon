package net.parostroj.timetable.model.validators;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
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
        if (event.getSource() instanceof Line) {
            Line line = (Line) event.getSource();
            if (event.getType() == Type.ATTRIBUTE
                    && event.getAttributeChange().checkName(Line.ATTR_LENGTH, Line.ATTR_SPEED)) {
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
            if (event.getSource() instanceof Line && event.getType() == Type.REMOVED
                    && event.getObject() instanceof LineTrack) {
                LineTrack lineTrack = (LineTrack) event.getObject();
                line.getFrom().getConnectors().getForLineTrack(lineTrack)
                        .ifPresent(c -> c.setLineTrack(Optional.empty()));
                line.getTo().getConnectors().getForLineTrack(lineTrack)
                        .ifPresent(c -> c.setLineTrack(Optional.empty()));
            }
        }
        return false;
    }
}
