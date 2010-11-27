package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.EDTModelAction;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action recalculates train stops.
 * 
 * @author jub
 */
public class RecalculateStopsAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(RecalculateStopsAction.class.getName());
    private ApplicationModel model;

    public RecalculateStopsAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Component top = ActionUtils.getTopLevelComponent(event.getSource());

        // get ratio
        String ratioStr = JOptionPane.showInputDialog(top, ResourceLoader.getString("recalculate.stops.ratio"), "0.5");

        if (ratioStr == null)
            return;

        // convert do double
        double cRatio = 1.0d;
        try {
            cRatio = Double.parseDouble(ratioStr);
        } catch (NumberFormatException e) {
            LOG.warn("Cannot convert to double: {}", ratioStr);
            return;
        }

        final double ratio = cRatio;

        ActionHandler.getInstance().executeAction(top, ResourceLoader.getString("wait.message.recalculate"),
                new EDTModelAction<Train>("Recalculation of stops") {

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
                    protected void processItem(Train train) throws Exception {
                        // convert stops ...
                        for (TimeInterval interval : train.getTimeIntervalList()) {
                            if (interval.isInnerStop()) {
                                // recalculate time ...
                                int time = interval.getLength();
                                time = this.convertTime(time, ratio);
                                // change stop time
                                train.changeStopTime(interval, time);
                            }
                        }
                        int time = 0;
                        // convert time before
                        if (train.getTimeBefore() != 0) {
                            time = train.getTimeBefore();
                            time = this.convertTime(time, ratio);
                            train.setTimeBefore(time);
                        }
                        // convert time after
                        if (train.getTimeAfter() != 0) {
                            time = train.getTimeAfter();
                            time = this.convertTime(time, ratio);
                            train.setTimeAfter(time);
                        }
                    }

                    private int convertTime(int time, double convertRatio) {
                        // recalculate
                        time = (int) (convertRatio * time);
                        // round to minutes
                        time = ((time + 40) / 60) * 60;
                        // do not change stop to 0
                        time = (time == 0) ? 1 : time;
                        return time;
                    }
                });
    }
}
