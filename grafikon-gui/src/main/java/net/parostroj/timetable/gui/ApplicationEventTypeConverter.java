package net.parostroj.timetable.gui;

import net.parostroj.timetable.gui.components.EventsViewerTypeConverter;

/**
 * Application model event type converter for Events Viewer.
 *
 * @author jub
 */
public class ApplicationEventTypeConverter implements EventsViewerTypeConverter {

    @Override
    public String getListString(Object event) {
        ApplicationModelEvent ame = (ApplicationModelEvent)event;
        return String.format("ApplicationEvent: %s", ame.getType().toString());
    }

    @Override
    public String getViewString(Object event) {
        ApplicationModelEvent ame = (ApplicationModelEvent)event;
        return String.format("ApplicationEvent: %s", ame.getType().toString());
    }

    @Override
    public Class<?> getEventClass() {
        return ApplicationModelEvent.class;
    }
}
