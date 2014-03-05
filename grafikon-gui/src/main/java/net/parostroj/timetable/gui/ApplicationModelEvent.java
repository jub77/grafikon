/*
 * ApplicationModelEvent.java
 *
 * Created on 2.9.2007, 15:14:11
 */

package net.parostroj.timetable.gui;

/**
 * Event that can be generated for model.
 * 
 * @author jub
 */
public class ApplicationModelEvent {

    private ApplicationModelEventType type;

    private ApplicationModel model;
    
    private Object object;

    /**
     * creates new event.
     * 
     * @param type type of the event
     * @param model associated model with the event
     */
    public ApplicationModelEvent(ApplicationModelEventType type, ApplicationModel model) {
        this.type = type;
        this.model = model;
    }

    /**
     * create new event
     * 
     * @param type type of the event
     * @param model associated mode
     * @param object object
     */
    public ApplicationModelEvent(ApplicationModelEventType type, ApplicationModel model, Object object) {
        this.type = type;
        this.model = model;
        this.object = object;
    }

    public ApplicationModelEventType getType() {
        return type;
    }

    public ApplicationModel getModel() {
        return model;
    }

    public Object getObject() {
        return object;
    }
}
