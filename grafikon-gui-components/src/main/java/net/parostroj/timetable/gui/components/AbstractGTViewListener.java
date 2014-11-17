package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.components.GraphicalTimetableView.GTViewListener;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TrainDiagram;

public abstract class AbstractGTViewListener implements GTViewListener {
    @Override
    public void diagramChanged(TrainDiagram diagram) {
    }

    @Override
    public void routeSelected(Route route) {
    }

    @Override
    public void settingsChanged(GTViewSettings settings) {
    }
}
