package net.parostroj.timetable.actions;

import java.util.UUID;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Checks loaded diagram. Adds missing values and other information.
 *
 * @author jub
 */
public class AfterLoadCheck {

    private static final Integer STATION_TRANSFER_TIME = 10;
    private static final Integer AXLE_LENGTH = 4500;
    private static final Integer WEIGHT_PER_AXLE = 14000;
    private static final Integer WEIGHT_PER_AXLE_EMPTY = 6000;
    private static final LengthUnit LENGTH_UNIT = LengthUnit.AXLE;

    public void check(TrainDiagram diagram) {
        // empty diagram doesn't have to be checked :)
        if (diagram == null) {
            return;
        }

        // add transfer time if missing
        if (diagram.getAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME, Object.class) == null)
            diagram.setAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME, STATION_TRANSFER_TIME);

        // axle length
        if (diagram.getAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE, Object.class) == null)
            diagram.setAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE, AXLE_LENGTH);

        // weight per axle
        if (diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE, Object.class) == null)
            diagram.setAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE, WEIGHT_PER_AXLE);

        // weight per axle (empty)
        if (diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY, Object.class) == null)
            diagram.setAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY, WEIGHT_PER_AXLE_EMPTY);

        // length unit
        if (diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT, Object.class) == null)
            diagram.setAttribute(TrainDiagram.ATTR_LENGTH_UNIT, LENGTH_UNIT);

        // add default trains cycle types (if already defined - no action)
        if (diagram.getDriverCycleType() == null) {
            diagram.addCyclesType(new TrainsCycleType(UUID.randomUUID().toString(), TrainsCycleType.DRIVER_CYCLE));
        }
        if (diagram.getEngineCycleType() == null) {
            diagram.addCyclesType(new TrainsCycleType(UUID.randomUUID().toString(), TrainsCycleType.ENGINE_CYCLE));
        }
        if (diagram.getTrainUnitCycleType() == null) {
            diagram.addCyclesType(new TrainsCycleType(UUID.randomUUID().toString(), TrainsCycleType.TRAIN_UNIT_CYCLE));
        }
    }
}
