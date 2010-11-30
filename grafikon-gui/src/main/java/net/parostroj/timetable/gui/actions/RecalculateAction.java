package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * This action recalculates all trains.
 * 
 * @author jub
 */
public class RecalculateAction extends AbstractAction {
    
    private static final Logger LOG = LoggerFactory.getLogger(RecalculateAction.class);
    
    public static interface TrainAction {
        public void execute(Train train) throws Exception;
    }

    private ApplicationModel model;

    public RecalculateAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionContext context = new ActionContext(ActionUtils.getTopLevelComponent(e.getSource()));
        ModelAction action = getAllTrainsAction(context, model.getDiagram(), new TrainAction() {
            
            @Override
            public void execute(Train train) throws Exception {
                train.recalculate();
            }
        }, ResourceLoader.getString("wait.message.recalculate"), "Recalculate");
        ModelAction action2 = new EventDispatchModelAction(context) {
            
            @Override
            protected void eventDispatchAction() {
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SET_DIAGRAM_CHANGED, model));
                // set back modified status (SET_DIAGRAM_CHANGED unfortunately clears the modified status)
                model.setModelChanged(true);
            }
        };
        ActionHandler.getInstance().execute(action);
        ActionHandler.getInstance().execute(action2);
    }
    
    public static ModelAction getAllTrainsAction(ActionContext context, final TrainDiagram diagram, final TrainAction trainAction, final String message, final String actionName) {
        ModelAction action = new CheckedModelAction(context) {
            
            private static final int CHUNK_SIZE = 10;

            @Override
            protected void action() {
                int size = diagram.getTrains().size();
                if (size == 0)
                    return;
                setWaitMessage(message);
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis(); 
                try {
                    int totalCount = (size - 1) / CHUNK_SIZE + 1;
                    CountDownLatch signal = new CountDownLatch(totalCount);
                    List<Train> batch = new LinkedList<Train>();
                    Iterator<Train> iterator = diagram.getTrains().iterator();
                    int cnt = 0;
                    while (iterator.hasNext()) {
                        Train train = iterator.next();
                        batch.add(train);
                        if (++cnt == CHUNK_SIZE) {
                            processChunk(batch, signal);
                            cnt = 0;
                            batch = new LinkedList<Train>();
                        }
                    }
                    if (batch.size() > 0) {
                        processChunk(batch, signal);
                    }
                    try {
                        signal.await();
                    } catch (InterruptedException e) {
                        LOG.error("Recalculate - await interrupted.", e);
                    }
                } finally {
                    LOG.debug("{} finished in {}ms", actionName, System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            private void processChunk(final Collection<Train> trains, final CountDownLatch signal) {
                ModelActionUtilities.runLaterInEDT(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (Train train : trains)
                                try {
                                    trainAction.execute(train);
                                } catch (Exception e) {
                                    LOG.error("Modification of train failed.", e);
                                }
                        } finally {
                            signal.countDown();
                        }
                    }
                });
            }
        };
        return action;
    }
}
