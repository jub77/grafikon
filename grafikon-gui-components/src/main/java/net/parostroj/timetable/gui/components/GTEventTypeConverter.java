package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.EventProcessing;

/**
 * GTEvent type converter for Events Viewer.
 *
 * @author jub
 */
public class GTEventTypeConverter implements EventsViewerTypeConverter {

    @Override
    public String getListString(Object event) {
        StringBuilder str = new StringBuilder();
        GTEventOutputVisitor visitor = new GTEventOutputVisitor(str, false);
        EventProcessing.visit((Event) event, visitor);
        return str.toString();
    }

    @Override
    public String getViewString(Object event) {
        StringBuilder str = new StringBuilder();
        GTEventOutputVisitor visitor = new GTEventOutputVisitor(str, true);
        EventProcessing.visit((Event) event, visitor);
        return str.toString();
    }

    @Override
    public Class<?> getEventClass() {
        return Event.class;
    }
}
