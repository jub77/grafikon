package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Highlighted trains.
 *
 * @author jub
 */
public interface HighlightedTrains {

    boolean isHighlighedInterval(TimeInterval interval);

    Color getColor(TimeInterval interval);
}
