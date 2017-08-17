package net.parostroj.timetable.gui.actions.execution;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;

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

    private final PropertyChangeSupport support;
    private WaitDialogState state;
    private String description;
    private int delay;
    private final Map<String, Object> attributes;
    private Component locationComponent;
    private boolean showProgress;
    private int progress;
    private String progressDescription;
    private boolean cancelled;
    private final String id;
    private long startTime;
    private boolean logTime;

    public ActionContext(String id, Component locationComponent) {
        this.support = new PropertyChangeSupport(this);
        this.state = WaitDialogState.HIDE;
        this.delay = DEFAULT_DELAY;
        this.showProgress = false;
        this.attributes = new HashMap<>();
        this.cancelled = false;
        this.id = id;
        this.locationComponent = locationComponent;
    }

    public ActionContext() {
        this((Component) null);
    }

    public ActionContext(Component locationComponent) {
        this("unknown", locationComponent);
    }

    protected void setState(WaitDialogState state) {
        WaitDialogState oldState = this.state;
        this.state = state;
        this.fireEventInEDT("state", oldState, state);
    }

    public void setWaitDialogVisible(boolean visible) {
        this.setState(visible ? WaitDialogState.SHOW : WaitDialogState.HIDE);
    }

    protected void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        this.fireEventInEDT("description", oldDescription, description);
    }

    public void setWaitMessage(String message) {
        this.setDescription(message);
    }

    public String getDescription() {
        return description;
    }

    public void setProgressDescription(String progressDescription) {
        String oldProgressDescription = this.progressDescription;
        this.progressDescription = progressDescription;
        this.fireEventInEDT("progressDescription", oldProgressDescription, progressDescription);
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setProgress(int progress) {
        int oldProgress = this.progress;
        this.progress = progress;
        this.fireEventInEDT("progress", oldProgress, this.progress);
    }

    public int getProgress() {
        return progress;
    }

    public void setLocationComponent(Component locationComponent) {
        this.locationComponent = locationComponent;
    }

    public Component getLocationComponent() {
        return locationComponent;
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    protected void removePropertyChangeListener(PropertyChangeListener listener) {
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

    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(getAttribute(name));
    }

    public Object removeAttribute(String name) {
        return attributes.remove(name);
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public boolean hasAttribute(String name) {
        return this.attributes.containsKey(name);
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    private void fireEventInEDT(final String name, final Object oldValue, final Object newValue) {
        GuiComponentUtils.runLaterInEDT(() -> support.firePropertyChange(name, oldValue, newValue));
    }

    protected void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    protected long getStartTime() {
        return startTime;
    }

    public String getId() {
        return id;
    }

    public void setLogTime(boolean logTime) {
        this.logTime = logTime;
    }

    public boolean isLogTime() {
        return logTime;
    }
}
