package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;

import net.parostroj.timetable.actions.TrainBuilder;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;

public class CopyTrainPM extends AbstractPM implements IPM<Train> {

    private WeakReference<Train> trainRef;

    final TextPM number = new TextPM();
    final TimePM time = new TimePM();
    final BooleanPM reversed = new BooleanPM();

    final OperationPM ok = new OperationPM();

    public CopyTrainPM() {
        PMManager.setup(this);
    }

    public void init(Train train) {
        this.trainRef = new WeakReference<Train>(train);

        time.setConverter(train.getDiagram().getTimeConverter());
        number.setText(train.getNumber());
        time.setTime(train.getStartTime());
        reversed.setBoolean(false);
    }

    private void writeResult() {
        // create copy of the train
        Train train = this.trainRef.get();
        if (train != null) {
            TrainDiagram diagram = train.getDiagram();
            int time = this.time.getTime();
            if (time == -1) {
                // select midnight if the time is not correct
                time = 0;
            }
            TrainBuilder builder = new TrainBuilder();
            String name = this.number.getText();
            Train newTrain = reversed.getBoolean() ?
                    builder.createReverseTrain(IdGenerator.getInstance().getId(), name, time, train) :
                    builder.createTrain(IdGenerator.getInstance().getId(), name, time, train);
            // add train to diagram
            diagram.getTrains().add(newTrain);
        }
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }
}
