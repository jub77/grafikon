package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Highlighted trains.
 *
 * @author jub
 */
public interface HighlightedTrains {

    public boolean isHighlighedInterval(TimeInterval interval);

    public Color getColor(TimeInterval interval);
}
