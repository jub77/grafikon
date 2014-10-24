package net.parostroj.timetable.output2.gt;

import java.util.Collection;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainsCycle;

/**
 * @author jub
 */
public class CirculationDrawParams {

    private final Collection<TrainsCycle> circulations;
    private final int from;
    private final int to;
    private final int step;


    public CirculationDrawParams(Collection<TrainsCycle> circulations) {
        this(4, circulations);
    }

    public CirculationDrawParams(int step, Collection<TrainsCycle> circulations) {
        this(0, TimeInterval.DAY, step, circulations);
    }

    public CirculationDrawParams(int from, int to, int step, Collection<TrainsCycle> circulations) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.circulations = circulations;
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

    public Collection<TrainsCycle> getCirculations() {
        return circulations;
    }
}
