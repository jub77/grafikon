package net.parostroj.timetable.output2.html;

import java.util.Locale;
import net.parostroj.timetable.output2.AbstractOutput;

/**
 * Output with locale parameter.
 *
 * @author jub
 */
abstract class OutputWithLocale extends AbstractOutput {

    private Locale locale;

    OutputWithLocale(Locale locale) {
        this.locale = locale;
    }

    protected Locale getLocale() {
        return locale;
    }
}
