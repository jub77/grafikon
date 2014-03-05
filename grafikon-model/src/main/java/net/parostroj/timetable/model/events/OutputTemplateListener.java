package net.parostroj.timetable.model.events;

/**
 * Output template listener.
 * 
 * @author jub
 */
public interface OutputTemplateListener extends GTListener {

    public void outputTemplateChanged(OutputTemplateEvent event);
}
