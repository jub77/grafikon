package net.parostroj.timetable.gui;

import net.parostroj.timetable.model.TrainDiagram;

/**
 * Checks loaded diagram. Adds missing values and other information.
 *
 * @author jub
 */
public class AfterSetChecker {

    private static final Integer STATION_TRANSFER_TIME = 10;
    private static final Double WEIGHT_RATIO_EMPTY = 0.17;
    private static final Double WEIGHT_RATIO_LOADED = 0.83;
    private static final Boolean LENGTH_IN_AXLES = Boolean.TRUE;

    public void check(TrainDiagram diagram) {
        // empty diagram doesn't have to be checked :)
        if (diagram == null) {
            return;
        }

        // add transfer time if missing
        if (diagram.getAttribute("station.transfer.time") == null) {
            diagram.setAttribute("station.transfer.time", STATION_TRANSFER_TIME);
        }

        // length in axles
        if (diagram.getAttribute("station.length.in.axles") == null) {
            diagram.setAttribute("station.length.in.axles", LENGTH_IN_AXLES);
        }
        // weight ratio
        if (diagram.getAttribute("weight.ratio.empty") == null) {
            diagram.setAttribute("weight.ratio.empty", WEIGHT_RATIO_EMPTY);
        }
        if (diagram.getAttribute("weight.ratio.loaded") == null) {
            diagram.setAttribute("weight.ratio.loaded", WEIGHT_RATIO_LOADED);
        }
    }
}
