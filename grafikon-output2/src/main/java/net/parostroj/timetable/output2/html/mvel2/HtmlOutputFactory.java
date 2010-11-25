package net.parostroj.timetable.output2.html.mvel2;

import java.lang.reflect.Constructor;
import java.util.*;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;

/**
 * Html output factory - mvel2.
 *
 * @author jub
 */
public class HtmlOutputFactory extends OutputFactory {

    private static final String TYPE = "mvel2";
    private static final Map<String, Class<? extends Output>> OUTPUT_TYPES;

    static {
        OUTPUT_TYPES = new HashMap<String, Class<? extends Output>>();
        OUTPUT_TYPES.put("starts", HtmlStartPositionsOutput.class);
        OUTPUT_TYPES.put("ends", HtmlEndPositionsOutput.class);
    }

    public HtmlOutputFactory() {
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
            throw new OutputException("Unknown type.");
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
