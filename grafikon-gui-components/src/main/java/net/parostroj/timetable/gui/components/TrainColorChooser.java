package net.parostroj.timetable.gui.components;

import java.awt.Color;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Train color chooser interface - for GTDraw.
 * 
 * @author jub
 */
public interface TrainColorChooser {
    public Color getIntervalColor(TimeInterval interval);
}
