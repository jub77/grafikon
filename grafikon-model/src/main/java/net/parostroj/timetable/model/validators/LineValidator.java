package net.parostroj.timetable.model.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.*;
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
        if (event.getSource() instanceof Line line) {
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
                    && event.getObject() instanceof LineTrack lineTrack) {
                TrainDiagram diagram = line.getDiagram();
                List<Train> trains = lineTrack.getTimeIntervalList().stream()
                        .map(TimeInterval::getTrain)
                        .distinct()
                        .toList();
                trains.forEach(train -> diagram.getTrains().remove(train));

                List<Route> routes = diagram.getRoutes().stream().filter(route -> route.contains(line)).toList();
                routes.forEach(route -> diagram.getRoutes().remove(route));

                line.getFrom().getConnectors().getForLineTrack(lineTrack)
                        .ifPresent(c -> c.setLineTrack(Optional.empty()));
                line.getTo().getConnectors().getForLineTrack(lineTrack)
                        .ifPresent(c -> c.setLineTrack(Optional.empty()));
            }
        }
        return false;
    }
}
