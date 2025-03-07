package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.events.DiagramChangeMessage;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.output2.gt.GTDraw;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class GraphicalTimetableViewBuilder {

    private final Map<GTViewSettings.Key, Object> settingOverlay = new EnumMap<>(GTViewSettings.Key.class);
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

    public GraphicalTimetableViewBuilder withSettings(GTViewSettings.Key key, Object value) {
        this.settingOverlay.put(key, value);
        return this;
    }

    public GraphicalTimetableView build() {
        GraphicalTimetableView view = save
                ? new GraphicalTimetableViewWithSave()
                : new GraphicalTimetableView();
        if (forTrains) {
            this.addForTrains(view);
        }
        if (!settingOverlay.isEmpty()) {
            this.updateSettings(view);
        }
        return view;
    }

    private void updateSettings(GraphicalTimetableView view) {
        GTViewSettings settings = view.getSettings();
        settingOverlay.forEach(settings::set);
        view.setSettings(settings);
    }

    private void addForTrains(GraphicalTimetableView view) {
        HighlightSelectTrains hts = new HighlightSelectTrains(mediator, Color.GREEN, view);
        view.setParameter(GTDraw.HIGHLIGHTED_TRAINS, hts);
        view.setRegionSelector(hts, TimeInterval.class);
        this.mediator.addColleague(
                message -> view.setTrainDiagram(((DiagramChangeMessage) message).diagram()),
                DiagramChangeMessage.class);
    }
}
