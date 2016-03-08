package net.parostroj.timetable.output2.gt;

import net.parostroj.timetable.model.Route;

/**
 * GTDrawFactory for GTView.
 *
 * @author jub
 */
public abstract class GTDrawFactory {

    public abstract GTDraw createInstance(GTDraw.Type type, GTDrawSettings settings, Route route, GTStorage storage);
}
