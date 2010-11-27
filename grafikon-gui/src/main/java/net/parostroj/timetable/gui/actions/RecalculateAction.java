package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.EDTModelAction;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * This action recalculates all trains.
 * 
 * @author jub
 */
public class RecalculateAction extends AbstractAction {

    private ApplicationModel model;

    public RecalculateAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionHandler.getInstance().executeAction(ActionUtils.getTopLevelComponent(e.getSource()),
                ResourceLoader.getString("wait.message.recalculate"), new EDTModelAction<Train>("Recalculation") {

                    private static final int CHUNK_SIZE = 10;
                    private Iterator<Train> iterator = model.getDiagram().getTrains().iterator();

                    @Override
                    protected void itemsFinished() {
                        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SET_DIAGRAM_CHANGED, model));
                        // set back modified status (SET_DIAGRAM_CHANGED unfortunately clears the modified status)
                        model.setModelChanged(true);
                    }

                    @Override
                    protected boolean prepareItems() throws Exception {
                        int number = 0;
                        while (number++ < CHUNK_SIZE && iterator.hasNext())
                            addItems(iterator.next());
                        return iterator.hasNext();
                    }

                    @Override
                    protected void processItem(Train item) throws Exception {
                        item.recalculate();
                    }
                });
    }
}
