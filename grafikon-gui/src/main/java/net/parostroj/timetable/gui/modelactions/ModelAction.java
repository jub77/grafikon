package net.parostroj.timetable.gui.modelactions;

/**
 * Action applied to model.
 * 
 * @author jub
 */
abstract class ModelAction {
    
    private String actionName;
    
    public ModelAction(String actionName) {
        this.actionName = actionName;
    }
    
    public ModelAction() {
        this("Action");
    }
    
    /**
     * Background operation.
     */
    public void run() {}
    
    /**
     * Action executed after run() method. It is executed in event dispatch thread.
     */
    public void afterRun() {}
    
    public String getActionName() {
        return actionName;
    }
}
