package net.parostroj.timetable.output2;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Abstract output - with charset.
 *
 * @author jub
 */
public abstract class OutputWithCharset extends OutputWithLocale {

    private final Charset charset;

    protected OutputWithCharset(Locale locale, Charset charset) {
        super(locale);
        this.charset = charset;
    }

    protected Charset getCharset() {
        return this.charset;
    }
}
