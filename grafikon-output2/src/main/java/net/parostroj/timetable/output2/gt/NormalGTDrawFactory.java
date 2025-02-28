package net.parostroj.timetable.output2.gt;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimeInterval;

/**
 * GTDraw factory - classic and with node tracks gt draw.
 *
 * @author jub
 */
public class NormalGTDrawFactory implements GTDrawFactory {

    @Override
    public GTDraw createInstance(GTDraw.Type type, GTDrawSettings settings, Route route, GTStorage storage) {
        TrainRegionCollector collector = (TrainRegionCollector) storage.getCollector(TimeInterval.class);
        TrainColors chooser = storage.getParameter(GTDraw.TRAIN_COLORS, TrainColors.class);
        HighlightedTrains hl = storage.getParameter(GTDraw.HIGHLIGHTED_TRAINS, HighlightedTrains.class);
        return switch (type) {
            case CLASSIC_STATION_STOPS -> new GTDrawClassicStationStops(settings, route, collector,
                    storage.getFilter(TimeInterval.class), chooser, hl);
            case WITH_TRACKS -> new GTDrawWithNodeTracks(settings, route, collector,
                    storage.getFilter(TimeInterval.class), chooser, hl);
            default -> new GTDrawClassic(settings, route, collector,
                    storage.getFilter(TimeInterval.class), chooser, hl);
        };
    }
}
