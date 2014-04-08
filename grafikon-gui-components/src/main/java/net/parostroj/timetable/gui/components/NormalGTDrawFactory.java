package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.components.GTViewSettings.Type;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimeInterval;

/**
 * GTDraw factory - classic and with node tracks gt draw.
 *
 * @author jub
 */
public class NormalGTDrawFactory extends GTDrawFactory {

    @Override
    public GTDraw createInstance(GTViewSettings settings, Route route, GTStorage storage) {
        TrainRegionCollector collector = (TrainRegionCollector) storage.getCollector(TimeInterval.class);
        GTDraw result = null;
        if (settings.get(Key.TYPE) == Type.CLASSIC) {
            result = new GTDrawClassic(settings, route, collector, storage.getFilter(TimeInterval.class));
        } else if (settings.get(Key.TYPE) == Type.WITH_TRACKS) {
            result = new GTDrawWithNodeTracks(settings, route, collector, storage.getFilter(TimeInterval.class));
        }
        return result;
    }
}
