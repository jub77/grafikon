package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.output2.gt.GTDraw;

import java.awt.*;

public class GraphicalTimetableViewBuilder {

    private Mediator mediator;
    private boolean save;
    private boolean forTrains;

    GraphicalTimetableViewBuilder() {}

    public GraphicalTimetableViewBuilder withSave() {
        this.save = true;
        return this;
    }

    public GraphicalTimetableViewBuilder forTrains(Mediator mediator) {
        this.forTrains = true;
        this.mediator = mediator;
        return this;
    }

    public GraphicalTimetableView build() {
        GraphicalTimetableView view = save
                ? new GraphicalTimetableViewWithSave()
                : new GraphicalTimetableView();
        if (forTrains) {
            this.addForTrains(view);
        }
        return view;
    }

    private void addForTrains(GraphicalTimetableView view) {
        HighlightSelectTrains hts = new HighlightSelectTrains(mediator, Color.GREEN, view);
        view.setParameter(GTDraw.HIGHLIGHTED_TRAINS, hts);
        view.setRegionSelector(hts, TimeInterval.class);
    }
}
