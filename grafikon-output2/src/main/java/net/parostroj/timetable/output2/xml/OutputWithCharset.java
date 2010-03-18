package net.parostroj.timetable.output2.xml;

import java.nio.charset.Charset;
import net.parostroj.timetable.output2.AbstractOutput;

/**
 * Abstract output - with charset.
 *
 * @author jub
 */
abstract class OutputWithCharset extends AbstractOutput {

    private Charset charset;

    OutputWithCharset(Charset charset) {
        this.charset = charset;
    }

    protected Charset getCharset() {
        return this.charset;
    }
}
