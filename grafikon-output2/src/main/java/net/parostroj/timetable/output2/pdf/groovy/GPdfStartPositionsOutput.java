package net.parostroj.timetable.output2.pdf.groovy;

import java.util.*;

import net.parostroj.timetable.output2.html.groovy.GspStartPositionsOutput;
import net.parostroj.timetable.output2.pdf.PdfTransformer;

/**
 * Starting position - pdf and groovy.
 *
 * @author jub
 */
public class GPdfStartPositionsOutput extends GPdfOutput {

    public GPdfStartPositionsOutput(Locale locale, PdfTransformer transformer) {
        super(new GspStartPositionsOutput(locale), transformer);
    }
}
