package net.parostroj.timetable.output2.gt;

import java.util.Collection;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainsCycle;

/**
 * @author jub
 */
public class CirculationDrawParams {

    private final Collection<TrainsCycle> circulations;
    private int from;
    private int to;
    private int step;


    public CirculationDrawParams(Collection<TrainsCycle> circulations) {
        this.circulations = circulations;
        this.from = 0;
        this.to = TimeInterval.DAY;
        this.step = 5;
    }

    public CirculationDrawParams(Collection<TrainsCycle> circulations, CirculationDrawParams params) {
        this(circulations);
        this.from = params.from;
        this.to = params.to;
        this.step = params.step;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getStep() {
        return step;
    }

    public CirculationDrawParams setFrom(int from) {
        this.from = from;
        return this;
    }

    public CirculationDrawParams setTo(int to) {
        this.to = to;
        return this;
    }

    public CirculationDrawParams setStep(int step) {
        this.step = step;
        return this;
    }

    public Collection<TrainsCycle> getCirculations() {
        return circulations;
    }
}
