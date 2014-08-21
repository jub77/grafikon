package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
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

    private final ApplicationModel model;

    public RecalculateAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionContext context = new ActionContext(GuiComponentUtils.getTopLevelComponent(e.getSource()));
        ModelAction recalculateAction = getAllTrainsAction(context, model.getDiagram(), new TrainAction() {

            @Override
            public void execute(Train train) throws Exception {
                train.recalculate();
            }
        }, ResourceLoader.getString("wait.message.recalculate"), "Recalculate");
        ActionHandler.getInstance().execute(recalculateAction);
    }

    public static ModelAction getAllTrainsAction(ActionContext context, final TrainDiagram diagram, final TrainAction trainAction, final String message, final String actionName) {
        ModelAction action = new CheckedModelAction(context) {

            private static final int CHUNK_SIZE = 10;
            private final CyclicBarrier barrier = new CyclicBarrier(2);

            @Override
            protected void action() {
                int size = diagram.getTrains().size();
                if (size == 0)
                    return;
                setWaitMessage(message);
                setWaitProgress(0);
                getActionContext().setShowProgress(true);
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    List<Train> batch = new LinkedList<Train>();
                    int totalCount = diagram.getTrains().size();
                    int counter = 0;
                    int batchCounter = 0;
                    for (Train train : diagram.getTrains()) {
                        batch.add(train);
                        if (++batchCounter == CHUNK_SIZE) {
                            processChunk(batch);
                            counter += batchCounter;
                            batchCounter = 0;
                            batch = new LinkedList<Train>();
                            setWaitProgress(100 * counter / totalCount);
                        }
                    }
                    if (batch.size() > 0) {
                        processChunk(batch);
                        setWaitProgress(100);
                    }
                } finally {
                    LOG.debug("{} finished in {}ms", actionName, System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            private void processChunk(final Collection<Train> trains) {
                GuiComponentUtils.runLaterInEDT(new Runnable() {

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
                            try {
                                barrier.await();
                            } catch (Exception e) {
                                LOG.error("Recalculate action - await interrupted.", e);
                            }
                        }
                    }
                });
                try {
                    barrier.await();
                } catch (Exception e) {
                    LOG.error("Recalculate - await interrupted.", e);
                }
            }
        };
        return action;
    }
}
