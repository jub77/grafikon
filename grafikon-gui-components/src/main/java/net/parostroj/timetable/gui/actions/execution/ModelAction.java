package net.parostroj.timetable.gui.actions.execution;

/**
 * Common interface for all background actions.
 * 
 * @author jub
 */
public interface ModelAction extends Runnable {

    public ActionContext getActionContext();
}
