package net.parostroj.timetable.output2.pdf;

import java.util.Set;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputFactory;

/**
 * Pdf output factory. Uses xsl-fo for creating the output.
 *
 * @author jub
 */
public class PdfOutputFactory extends OutputFactory {

    private static final String TYPE = "pdf";

    public PdfOutputFactory() {
    }

    @Override
    public Set<String> getOutputTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Output createOutput(String type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
