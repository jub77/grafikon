package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Train color chooser interface - for GTDraw.
 *
 * @author jub
 */
@FunctionalInterface
public interface TrainColorChooser {

    public Color getIntervalColor(TimeInterval interval);
}
