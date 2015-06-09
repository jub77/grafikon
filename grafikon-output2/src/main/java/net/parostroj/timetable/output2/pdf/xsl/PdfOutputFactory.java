package net.parostroj.timetable.output2.pdf.xsl;

import java.lang.reflect.Constructor;
import java.util.*;

import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;

/**
 * Pdf output factory. Uses xsl-fo for creating the output.
 *
 * @author jub
 */
public class PdfOutputFactory extends OutputFactory {

    private static final String TYPE = "pdf";

    private static final Map<String, Class<? extends Output>> OUTPUT_TYPES;

    static {
        OUTPUT_TYPES = new LinkedHashMap<String, Class<? extends Output>>();
        OUTPUT_TYPES.put("starts", PdfStartPositionsOutput.class);
    }

    public PdfOutputFactory() {
    }

    private Locale getLocale() {
        Locale locale = (Locale) this.getParameter("locale");
        if (locale == null)
            locale = Locale.getDefault();
        return locale;
    }

    @Override
    public Set<String> getOutputTypes() {
        return OUTPUT_TYPES.keySet();
    }

    @Override
    public Output createOutput(String type) throws OutputException {
        Class<? extends Output> outputClass = OUTPUT_TYPES.get(type);
        if (outputClass == null)
            throw new OutputException("Unknown type: " + type);
        try {
            Constructor<? extends Output> constructor = outputClass.getConstructor(Locale.class);
            return constructor.newInstance(this.getLocale());
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
