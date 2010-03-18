/*
 * TCDelegate.java
 * 
 * Created on 16.9.2007, 14:31:31
 */

package net.parostroj.timetable.gui.views;

import java.util.List;
import javax.swing.JComponent;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.model.*;

/**
 * Delegate for actions over trains cycles.
 * 
 * @author jub
 */
public interface TCDelegate {
    public enum Action {
        NEW_CYCLE, DELETE_CYCLE, MODIFIED_CYCLE, SELECTED_CHANGED; 
    }
    
    public void setSelectedCycle(ApplicationModel model, TrainsCycle cycle);
    
    public TrainsCycle getSelectedCycle(ApplicationModel model);
    
    public TrainsCycleType getType();
    
    public void fireEvent(Action action, ApplicationModel model, TrainsCycle cycle);
    
    public Action transformEventType(ApplicationModelEventType type);
    
    public List<TrainsCycleItem> getTrainCycles(Train train);
    
    public String getTrainCycleErrors(TrainsCycle cycle, TrainDiagram diagram);
    
    public void showEditDialog(JComponent component, ApplicationModel model);
    
    public String getCycleDescription(ApplicationModel model);

    public boolean isOverlappingEnabled();
}
