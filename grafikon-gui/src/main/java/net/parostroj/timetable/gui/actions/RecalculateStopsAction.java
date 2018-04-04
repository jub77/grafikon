package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.RecalculateAction.TrainAction;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action recalculates train stops.
 *
 * @author jub
 */
public class RecalculateStopsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(RecalculateStopsAction.class);
    private final ApplicationModel model;

    public RecalculateStopsAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Component top = GuiComponentUtils.getTopLevelComponent(event.getSource());

        // get ratio
        String ratioStr = (String) JOptionPane.showInputDialog(top, ResourceLoader.getString("recalculate.stops.ratio"),
                null, JOptionPane.QUESTION_MESSAGE, null, null, "0.5");

        if (ratioStr == null) {
            return;
        }

        // convert do double
        double cRatio = 1.0d;
        try {
            cRatio = Double.parseDouble(ratioStr);
        } catch (NumberFormatException e) {
            log.warn("Cannot convert to double: {}", ratioStr);
            return;
        }

        final double ratio = cRatio;

        final TrainDiagram diagram = model.getDiagram();

        TrainAction trainAction = new TrainAction() {

            @Override
            public void execute(Train train) throws Exception {
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
                TimeConverter converter = diagram.getTimeConverter();
                time = converter.round(time);
                // do not change stop to 0
                return Math.max(time, converter.getRounding().getMin());
            }
        };


        RxActionHandler.getInstance().newExecution("recalculate_stops",
                GuiComponentUtils.getTopLevelComponent(event.getSource()),
                model.getDiagram())
            .onBackground()
            .logTime()
            .setMessage(ResourceLoader.getString("wait.message.recalculate"))
            .split(d -> d.getTrains(), 10)
            .addEdtBatchConsumer((context, train) -> {
                trainAction.execute(train);
            }).execute();
    }
}
