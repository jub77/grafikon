package net.parostroj.timetable.output2.gt;

import net.parostroj.timetable.output2.gt.GTDraw.OutputType;
import net.parostroj.timetable.output2.gt.GTDraw.Type;

/**
 * Parameters for GTDrawOutput.
 *
 * @author jub
 */
public class GTDrawParams {

    private final GTDraw.Type type;
    private final GTDrawSettings settings;
    private final GTDraw.OutputType outputType;

    public GTDrawParams() {
        this(GTDraw.Type.CLASSIC, GTDrawSettings.create(), GTDraw.OutputType.SVG);
    }

    public GTDrawParams(Type type, GTDrawSettings settings, OutputType outputType) {
        if (outputType == null || type == null || settings == null) {
            throw new NullPointerException("Parameters cannot be null");
        }
        this.type = type;
        this.settings = settings;
        this.outputType = outputType;
    }

    public GTDrawSettings getSettings() {
        return settings;
    }

    public GTDraw.Type getType() {
        return type;
    }

    public GTDraw.OutputType getOutputType() {
        return outputType;
    }
}
