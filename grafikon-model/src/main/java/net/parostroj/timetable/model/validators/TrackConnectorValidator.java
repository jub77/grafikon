package net.parostroj.timetable.model.validators;

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
        if (event.getSource() instanceof TrackConnector connector) {
            if (event.getType() == Type.OBJECT_ATTRIBUTE
                    && event.getAttributeChange().checkName(TrackConnectorSwitch.ATTR_STRAIGHT)) {
                TrackConnectorSwitch sw = (TrackConnectorSwitch) event.getObject();
                sw.getNodeTrack().getTimeIntervalList().stream()
                        .map(TimeInterval::getTrain)
                        .distinct()
                        .toList()
                        .forEach(Train::recalculate);
            } else if (event.getType() == Type.ATTRIBUTE
                    && event.getAttributeChange().checkName(TrackConnector.ATTR_ORIENTATION)) {
                connector.getLineTrack().ifPresent(lt -> lt.getTimeIntervalList().stream()
                        .map(TimeInterval::getTrain)
                        .distinct()
                        .toList()
                        .forEach(Train::recalculate));
            }
        }
        return false;
    }
}
