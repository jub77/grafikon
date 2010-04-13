package net.parostroj.timetable.output2.html.groovy;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;

/**
 * Html output factory - groovy.
 *
 * @author jub
 */
public class GspOutputFactory extends OutputFactory {

    private static final String TYPE = "groovy";
    private static Map<String, Class<? extends Output>> OUTPUT_TYPES;

    static {
        OUTPUT_TYPES = new HashMap<String, Class<? extends Output>>();
        OUTPUT_TYPES.put("starts", GspStartPositionsOutput.class);
        OUTPUT_TYPES.put("ends", GspEndPositionsOutput.class);
        OUTPUT_TYPES.put("stations", GspStationTimetablesOutput.class);
        OUTPUT_TYPES.put("train_unit_cycles", GspTrainUnitCyclesOutput.class);
    }

    public GspOutputFactory() {
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
