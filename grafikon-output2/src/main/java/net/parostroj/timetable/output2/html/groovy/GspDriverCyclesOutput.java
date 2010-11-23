package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.Template;
import java.io.*;
import java.util.*;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.DriverCycles;
import net.parostroj.timetable.output2.impl.DriverCyclesExtractor;
import net.parostroj.timetable.output2.util.SelectionHelper;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Implements html output for driver cycles.
 *
 * @author jub
 */
public class GspDriverCyclesOutput extends GspOutput {

    public GspDriverCyclesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract driver cycles
            DriverCyclesExtractor ece = new DriverCyclesExtractor(diagram, SelectionHelper.selectCycles(params, diagram, TrainsCycleType.DRIVER_CYCLE), true);
            DriverCycles cycles = ece.getDriverCycles();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cycles", cycles);
            ResourceHelper.addTextsToMap(map, "dc_", this.getLocale(), "texts/html_texts");

            Template template = this.getTemplate(params, "templates/groovy/driver_cycles.gsp", this.getClass().getClassLoader());
            Writable result = template.make(map);
            Writer writer = new OutputStreamWriter(stream, "utf-8");
            result.writeTo(writer);
            writer.flush();
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
