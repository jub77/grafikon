package net.parostroj.timetable.output2.gt;

import net.parostroj.timetable.output2.gt.GTDraw.Type;

/**
 * Parameters for GTDrawOutput.
 *
 * @author jub
 */
public class GTDrawParams {

    private final GTDraw.Type type;
    private final GTDrawSettings settings;

    public GTDrawParams() {
        this(GTDraw.Type.CLASSIC, GTDrawSettings.create());
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

    public GTDraw.Type getType() {
        return type;
    }
}
