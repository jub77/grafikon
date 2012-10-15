package net.parostroj.timetable.gui.components;

/**
 * Type converter.
 *
 * @author jub
 */
public interface EventsViewerTypeConverter {

    public String getListString(Object event);

    public String getViewString(Object event);

    public Class<?> getEventClass();
}
