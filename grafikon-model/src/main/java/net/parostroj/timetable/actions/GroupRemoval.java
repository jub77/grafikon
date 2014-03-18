package net.parostroj.timetable.actions;

import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Group handler.
 *
 * @author jub
 */
public class GroupRemoval {

    private final TrainDiagram diagram;

    public GroupRemoval(TrainDiagram diagram) {
        super();
        this.diagram = diagram;
    }

    public void removeGroup(Group group) {
        // remove group from trains ...
        for (Train train : diagram.getTrains()) {
            Group trainGroup = train.getAttributes().get(Train.ATTR_GROUP, Group.class);
            if (group.equals(trainGroup))
                train.removeAttribute(Train.ATTR_GROUP);
        }
        // remove from diagram
        diagram.removeGroup(group);
    }
}
