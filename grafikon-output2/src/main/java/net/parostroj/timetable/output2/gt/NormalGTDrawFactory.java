package net.parostroj.timetable.output2.gt;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimeInterval;

/**
 * GTDraw factory - classic and with node tracks gt draw.
 *
 * @author jub
 */
public class NormalGTDrawFactory extends GTDrawFactory {

    @Override
    public GTDraw createInstance(GTDraw.Type type, GTDrawSettings settings, Route route, GTStorage storage) {
        TrainRegionCollector collector = (TrainRegionCollector) storage.getCollector(TimeInterval.class);
        TrainColorChooser chooser = storage.getParameter(GTDraw.TRAIN_COLOR_CHOOSER, TrainColorChooser.class);
        HighlightedTrains hl = storage.getParameter(GTDraw.HIGHLIGHTED_TRAINS, HighlightedTrains.class);
        GTDraw result = null;
        switch (type) {
            case CLASSIC_STATION_STOPS:
                result = new GTDrawClassicStationStops(settings, route, collector,
                        storage.getFilter(TimeInterval.class), chooser, hl);
                break;
            case WITH_TRACKS:
                result = new GTDrawWithNodeTracks(settings, route, collector,
                        storage.getFilter(TimeInterval.class), chooser, hl);
                break;
            default:
                result = new GTDrawClassic(settings, route, collector,
                        storage.getFilter(TimeInterval.class), chooser, hl);
                break;
        }
        return result;
    }
}
