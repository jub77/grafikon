package net.parostroj.timetable.output2.html.groovy;

import java.util.Locale;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputFactory;

/**
 * Html output factory - groovy.
 *
 * @author jub
 */
public class GspOutputFactory extends OutputFactory {

    private static final String TYPE = "groovy";

    public GspOutputFactory() {
    }

    private Locale getLocale() {
        Locale locale = (Locale) this.getParameter("locale");
        if (locale == null)
            locale = Locale.getDefault();
        return locale;
    }

    @Override
    public Output createOutput(String type) {
        if ("starts".equals(type))
            return new GspStartPositionsOutput(this.getLocale());
        else if ("ends".equals(type))
            return new GspEndPositionsOutput(this.getLocale());
        else if ("stations".equals(type))
            return new GspStationTimetablesOutput(this.getLocale());
        else
            throw new RuntimeException("Unknown type.");
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
