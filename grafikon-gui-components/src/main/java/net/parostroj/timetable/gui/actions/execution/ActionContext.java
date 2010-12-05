package net.parostroj.timetable.gui.actions.execution;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * Context for sequence of model actions.
 *
 * @author jub
 */
public class ActionContext {

    public static enum WaitDialogState {
        HIDE, SHOW;
    }
    
    private static final int DEFAULT_DELAY = 200;

    private PropertyChangeSupport support;
    private WaitDialogState state;
    private String description;
    private int delay;
    private Map<String, Object> attributes;
    private Component locationComponent;
    
    public ActionContext() {
        this.support = new PropertyChangeSupport(this);
        this.state = WaitDialogState.HIDE;
        this.delay = DEFAULT_DELAY;
        this.attributes = new HashMap<String, Object>();
    }
    
    public ActionContext(Component locationComponent) {
        this();
        this.locationComponent = locationComponent;
    }
    
    public void setState(WaitDialogState state) {
        WaitDialogState oldState = this.state;
        this.state = state;
        this.fireEventInEDT("state", oldState, state);
    }
    
    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        this.fireEventInEDT("description", oldDescription, description);
    }
    
    public void setLocationComponent(Component locationComponent) {
        this.locationComponent = locationComponent;
    }
    
    public Component getLocationComponent() {
        return locationComponent;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
    
    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    public int getDelay() {
        return delay;
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
    public Object removeAttribute(String name) {
        return attributes.remove(name);
    }
    
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }
    
    private void fireEventInEDT(final String name, final Object oldValue, final Object newValue) {
        ModelActionUtilities.runLaterInEDT(new Runnable() {
            
            @Override
            public void run() {
                support.firePropertyChange(name, oldValue, newValue);
            }
        });
    }
}
