package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.model.Route;

/**
 * GTDrawFactory for GTView.
 *
 * @author jub
 */
public abstract class GTDrawFactory {

    public abstract GTDraw createInstance(GTViewSettings settings, Route route, GTStorage storage);
}
