package net.parostroj.timetable.model.validators;

import static java.util.stream.Collectors.toList;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrackConnectorSwitch;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * Validator for changes in {@link TrackConnector}.
 *
 * @author jub
 */
public class TrackConnectorValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrackConnector) {
            if (event.getType() == Type.OBJECT_ATTRIBUTE
                    && event.getAttributeChange().checkName(TrackConnectorSwitch.ATTR_STRAIGHT)) {
                TrackConnectorSwitch sw = (TrackConnectorSwitch) event.getObject();
                sw.getNodeTrack().getTimeIntervalList().stream()
                        .map(TimeInterval::getTrain)
                        .distinct()
                        .collect(toList())
                        .forEach(Train::recalculate);
            } else if (event.getType() == Type.ATTRIBUTE
                    && event.getAttributeChange().checkName(TrackConnector.ATTR_ORIENTATION)) {
                TrackConnector connector = (TrackConnector) event.getSource();
                connector.getLineTrack().ifPresent(lt -> lt.getTimeIntervalList().stream()
                        .map(TimeInterval::getTrain)
                        .distinct()
                        .collect(toList())
                        .forEach(Train::recalculate));
            }
        }
        return false;
    }
}
