package net.parostroj.timetable.gui;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Checks loaded diagram. Adds missing values and other information.
 *
 * @author jub
 */
public class AfterSetChecker {

    private static final Integer STATION_TRANSFER_TIME = 10;
    private static final Integer AXLE_LENGTH = 4500;
    private static final Integer WEIGHT_PER_AXLE = 17000;
    private static final Integer WEIGHT_PER_AXLE_EMPTY = 6000;
    private static final LengthUnit LENGTH_UNIT = LengthUnit.AXLE;

    public void check(TrainDiagram diagram) {
        // empty diagram doesn't have to be checked :)
        if (diagram == null) {
            return;
        }

        // add transfer time if missing
        if (diagram.getAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME) == null)
            diagram.setAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME, STATION_TRANSFER_TIME);

        // axle length
        if (diagram.getAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE) == null)
            diagram.setAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE, AXLE_LENGTH);

        // weight per axle
        if (diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE) == null)
            diagram.setAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE, WEIGHT_PER_AXLE);

        // weight per axle (empty)
        if (diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY) == null)
            diagram.setAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY, WEIGHT_PER_AXLE_EMPTY);

        // length unit
        if (diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT) == null)
            diagram.setAttribute(TrainDiagram.ATTR_LENGTH_UNIT, LENGTH_UNIT);
    }
}
