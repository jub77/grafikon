package net.parostroj.timetable.output2.xml;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;

/**
 * Xml output factory.
 *
 * @author jub
 */
public class XmlOutputFactory extends OutputFactory {

    private static final String TYPE = "xml";
    private static final Map<String, Class<? extends Output>> OUTPUT_TYPES;

    static {
        OUTPUT_TYPES = new LinkedHashMap<>();
        OUTPUT_TYPES.put("starts", XmlStartPositionsOutput.class);
        OUTPUT_TYPES.put("ends", XmlEndPositionsOutput.class);
        OUTPUT_TYPES.put("stations", XmlStationTimetablesOutput.class);
        OUTPUT_TYPES.put("train_unit_cycles", XmlTrainUnitCyclesOutput.class);
        OUTPUT_TYPES.put("engine_cycles", XmlEngineCyclesOutput.class);
        OUTPUT_TYPES.put("driver_cycles", XmlDriverCyclesOutput.class);
        OUTPUT_TYPES.put("trains", XmlTrainTimetablesOutput.class);
        OUTPUT_TYPES.put("custom_cycles", XmlCustomCyclesOutput.class);
    }

    private Charset getCharset() {
        Charset charset = (Charset) this.getParameter("charset");
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return charset;
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
            Constructor<? extends Output> constructor = outputClass.getConstructor(Locale.class, Charset.class);
            return constructor.newInstance(this.getLocale(), this.getCharset());
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
