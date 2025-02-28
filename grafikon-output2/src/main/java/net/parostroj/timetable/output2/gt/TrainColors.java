package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Train colors interface - for GTDraw.
 *
 * @author jub
 */
@FunctionalInterface
public interface TrainColors {

    Color getIntervalColor(TimeInterval interval);
}
