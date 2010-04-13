package net.parostroj.timetable.output2.xml;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    private static Map<String, Class<? extends Output>> OUTPUT_TYPES;

    static {
        OUTPUT_TYPES = new HashMap<String, Class<? extends Output>>();
        OUTPUT_TYPES.put("starts", XmlStartPositionsOutput.class);
        OUTPUT_TYPES.put("ends", XmlEndPositionsOutput.class);
        OUTPUT_TYPES.put("stations", XmlStationTimetablesOutput.class);
        OUTPUT_TYPES.put("train_unit_cycles", XmlTrainUnitCyclesOutput.class);
        OUTPUT_TYPES.put("engine_cycles", XmlEngineCyclesOutput.class);
    }

    private Charset getCharset() {
        Charset charset = (Charset) this.getParameter("charset");
        if (charset == null) {
            charset = Charset.forName("utf-8");
        }
        return charset;
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
            Constructor<? extends Output> constructor = outputClass.getConstructor(Charset.class);
            return constructor.newInstance(this.getCharset());
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
