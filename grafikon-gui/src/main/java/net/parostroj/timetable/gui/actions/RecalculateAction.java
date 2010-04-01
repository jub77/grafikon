package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * This action recalculates all trains.
 *
 * @author jub
 */
public class RecalculateAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(RecalculateAction.class.getName());
    private ApplicationModel model;

    public RecalculateAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // get top level ancestor ...
        ActionHandler.getInstance().executeAction(
                ActionUtils.getTopLevelComponent(e.getSource()),
                ResourceLoader.getString("wait.message.recalculate"),
                new SwingWorker<Void, Train>() {

                    private Lock lock = new ReentrantLock();
                    private Condition condition = lock.newCondition();
                    private boolean chunksFinished = false;
                    private static final int CHUNK_SIZE = 10;

                    @Override
                    protected Void doInBackground() throws Exception {
                        // recalculate all trains
                        lock.lock();
                        try {
                            int cnt = 0;
                            for (Train train : model.getDiagram().getTrains()) {
                                publish(train);
                                if (++cnt >= CHUNK_SIZE) {
                                    chunksFinished = false;
                                    while (!chunksFinished) {
                                        condition.await();
                                    }
                                    cnt = 0;
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SET_DIAGRAM_CHANGED, model));
                        // set back modified status (SET_DIAGRAM_CHANGED unfortunately clears the modified status)
                        model.setModelChanged(true);
                    }

                    @Override
                    protected void process(List<Train> chunks) {
                        LOG.finest("Recalculate chunk of trains. Size: " + chunks.size());
                        lock.lock();
                        try {
                            for (Train train : chunks) {
                                train.recalculate();
                            }
                            chunksFinished = true;
                            condition.signalAll();
                        } finally {
                            lock.unlock();
                        }
                    }
                });
    }
}
