package net.parostroj.timetable.gui.events;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

public record TrainSelectionMessage(Train train, TimeInterval interval) {}
