package net.parostroj.timetable.gui.utils;

/**
 * Action applied to model.
 * 
 * @author jub
 */
public interface ModelAction {
    /**
     * Background operation.
     */
    public void run();
    
    /**
     * Action executed after run() method. It is executed in event dispatch thread.
     */
    public void afterRun();
}
