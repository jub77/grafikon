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
    private int widthInChars;
    private String title;
    private float zoom;
    private CirculationDrawColors colors;

    public CirculationDrawParams(Collection<TrainsCycle> circulations) {
        this.circulations = circulations;
        this.from = 0;
        this.to = TimeInterval.DAY;
        this.widthInChars = 4;
        this.zoom = 1f;
    }

    public CirculationDrawParams(Collection<TrainsCycle> circulations, CirculationDrawParams params) {
        this(circulations);
        this.from = params.from;
        this.to = params.to;
        this.widthInChars = params.widthInChars;
        this.zoom = params.zoom;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getWidthInChars() {
        return widthInChars;
    }

    public String getTitle() {
        return title;
    }

    public float getZoom() {
        return zoom;
    }

    public CirculationDrawColors getColors() {
        return colors;
    }

    public CirculationDrawParams setFrom(int from) {
        this.from = from;
        return this;
    }

    public CirculationDrawParams setTo(int to) {
        this.to = to;
        return this;
    }

    public CirculationDrawParams setWidthInChars(int widthInChars) {
        this.widthInChars = widthInChars;
        return this;
    }

    public CirculationDrawParams setTitle(String title) {
        this.title = title;
        return this;
    }

    public Collection<TrainsCycle> getCirculations() {
        return circulations;
    }

    public CirculationDrawParams setZoom(float zoom) {
        this.zoom = zoom;
        return this;
    }

    public CirculationDrawParams setColors(CirculationDrawColors colors) {
        this.colors = colors;
        return this;
    }
}
