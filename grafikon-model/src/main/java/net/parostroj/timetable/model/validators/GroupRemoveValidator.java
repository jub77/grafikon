package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * Group remove correction.
 *
 * @author jub
 */
public class GroupRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public GroupRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED
                && event.getObject() instanceof Group) {
            Group group = (Group) event.getObject();
            // remove group from trains ...
            for (Train train : diagram.getTrains()) {
                Group trainGroup = train.getAttributes().get(Train.ATTR_GROUP, Group.class);
                if (group.equals(trainGroup))
                    train.removeAttribute(Train.ATTR_GROUP);
            }
            return true;
        }
        return false;
    }
}
