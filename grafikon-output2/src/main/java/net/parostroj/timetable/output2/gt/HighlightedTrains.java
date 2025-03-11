package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Highlighted trains.
 *
 * @author jub
 */
@FunctionalInterface
public interface HighlightedTrains {

    Color getHighlightColor(TimeInterval interval);
}
