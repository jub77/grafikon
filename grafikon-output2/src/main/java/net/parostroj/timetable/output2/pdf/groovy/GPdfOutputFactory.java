package net.parostroj.timetable.output2.pdf.groovy;

import java.lang.reflect.Constructor;
import java.util.*;

import javax.xml.transform.TransformerFactory;

import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;
import net.parostroj.timetable.output2.pdf.PdfTransformer;

/**
 * Pdf output factory. Uses fo for creating the output.
 *
 * @author jub
 */
public class GPdfOutputFactory extends OutputFactory {

    private static final String TYPE = "pdf.groovy";

    private static final Map<String, Class<? extends Output>> OUTPUT_TYPES;

    static {
        OUTPUT_TYPES = new LinkedHashMap<String, Class<? extends Output>>();
        OUTPUT_TYPES.put("starts", GPdfStartPositionsOutput.class);
    }

    private final PdfTransformer transformer;

    public GPdfOutputFactory() {
        this.transformer = new PdfTransformer(TransformerFactory.newInstance());
    }

    private Locale getLocale() {
        Locale locale = (Locale) this.getParameter("locale");
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    @Override
    public Set<String> getOutputTypes() {
        return OUTPUT_TYPES.keySet();
    }

    @Override
    public Output createOutput(String type) throws OutputException {
        Class<? extends Output> outputClass = OUTPUT_TYPES.get(type);
        if (outputClass == null) {
            throw new OutputException("Unknown type: " + type);
        }
        try {
            Constructor<? extends Output> constructor = outputClass.getConstructor(Locale.class, PdfTransformer.class);
            return constructor.newInstance(this.getLocale(), transformer);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
