package net.parostroj.timetable.output2.gt;


/**
 * @author jub
 */
public class CirculationDrawParams {

    private final int from;
    private final int to;
    private final int step;
    private final FileOutputType outputType;

    public CirculationDrawParams(int from, int to, int step, FileOutputType outputType) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.outputType = outputType;
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

    public FileOutputType getOutputType() {
        return outputType;
    }
}
