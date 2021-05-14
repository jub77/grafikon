package net.parostroj.timetable.output2;

import java.util.Locale;

/**
 * Output with locale parameter.
 *
 * @author jub
 */
public abstract class OutputWithLocale extends OutputWithDiagramStream {

    private final Locale locale;

    protected OutputWithLocale(Locale locale) {
        this.locale = locale;
    }

    protected Locale getLocale() {
        return locale;
    }
}
