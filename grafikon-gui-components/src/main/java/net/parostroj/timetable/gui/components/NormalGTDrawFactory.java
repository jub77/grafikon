package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimeInterval;

/**
 * GTDraw factory - classic and with node tracks gt draw.
 *
 * @author jub
 */
public class NormalGTDrawFactory extends GTDrawFactory {

    @Override
    public GTDraw createInstance(GTViewSettings.Type type, GTDrawSettings settings, Route route, GTStorage storage) {
        TrainRegionCollector collector = (TrainRegionCollector) storage.getCollector(TimeInterval.class);
        GTDraw result = null;
        switch (type) {
            case CLASSIC_STATION_STOPS:
                result = new GTDrawClassicStationStops(settings, route, collector, storage.getFilter(TimeInterval.class));
                break;
            case WITH_TRACKS:
                result = new GTDrawWithNodeTracks(settings, route, collector, storage.getFilter(TimeInterval.class));
                break;
            default:
                result = new GTDrawClassic(settings, route, collector, storage.getFilter(TimeInterval.class));
                break;
        }
        return result;
    }
}
