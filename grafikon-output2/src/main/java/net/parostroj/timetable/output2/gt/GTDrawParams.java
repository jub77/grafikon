package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;

import net.parostroj.timetable.output2.gt.GTDraw.Type;
import net.parostroj.timetable.output2.gt.GTDrawSettings.Key;

/**
 * Parameters for GTDrawOutput.
 *
 * @author jub
 */
public class GTDrawParams {

    private final Type type;
    private final GTDrawSettings settings;

    public GTDrawParams() {
        this(Type.CLASSIC, GTDrawSettings.create());
    }

    public GTDrawParams(Type type) {
        this(type, GTDrawSettings.create());
    }

    public GTDrawParams(Type type, GTDrawSettings settings) {
        if (type == null || settings == null) {
            throw new NullPointerException("Parameters cannot be null");
        }
        this.type = type;
        this.settings = settings;
    }

    public GTDrawSettings getSettings() {
        return settings;
    }

    public Type getType() {
        return type;
    }

    public void setSize(int x, int y) {
        this.settings.set(Key.SIZE, new Dimension(x, y));
    }

    public void setZoom(float zoom) {
        this.settings.set(Key.ZOOM, zoom);
    }

    public void setStart(int time) {
        this.settings.set(Key.START_TIME, time);
    }

    public void setEnd(int time) {
        this.settings.set(Key.END_TIME, time);
    }

    public void setTechnologicalTime(boolean technologicalTime) {
        this.settings.setOption(Key.TECHNOLOGICAL_TIME, technologicalTime);
    }

    public void setArrivalDepartureDigits(boolean arrivalDepartureDigits) {
        this.settings.setOption(Key.ARRIVAL_DEPARTURE_DIGITS, arrivalDepartureDigits);
    }

    public void setExtendedLines(boolean extendedLines) {
        this.settings.setOption(Key.EXTENDED_LINES, extendedLines);
    }
}
