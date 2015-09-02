package net.parostroj.timetable.output2.gt;

import java.awt.Color;

import net.parostroj.timetable.model.TrainsCycleItem;

@FunctionalInterface
public interface CirculationDrawColors {

    default Color getColor(String location) {
        return getColor(location, null);
    }

    Color getColor(String location, TrainsCycleItem circulationItem);
}
