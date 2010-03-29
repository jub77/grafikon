package net.parostroj.timetable.output2;

import java.nio.charset.Charset;

/**
 * Abstract output - with charset.
 *
 * @author jub
 */
abstract public class OutputWithCharset extends OutputWithDiagramStream {

    private Charset charset;

    public OutputWithCharset(Charset charset) {
        this.charset = charset;
    }

    protected Charset getCharset() {
        return this.charset;
    }
}
