package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.Template;
import java.io.*;
import java.util.*;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.util.SelectionHelper;
import net.parostroj.timetable.output2.impl.TrainUnitCycle;
import net.parostroj.timetable.output2.impl.TrainUnitCyclesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Implements html output for train unit cycles.
 *
 * @author jub
 */
public class GspTrainUnitCyclesOutput extends GspOutput {

    public GspTrainUnitCyclesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        try {
            // extract positions
            TrainUnitCyclesExtractor tuce = new TrainUnitCyclesExtractor(SelectionHelper.selectCycles(params, diagram, TrainsCycleType.TRAIN_UNIT_CYCLE));
            List<TrainUnitCycle> cycles = tuce.getTrainUnitCycles();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cycles", cycles);
            ResourceHelper.addTextsToMap(map, "tuc_", this.getLocale(), "texts/html_texts");

            Template template = this.createTemplate(params, "templates/groovy/train_unit_cycles.gsp", this.getClass().getClassLoader());
            Writable result = template.make(map);
            Writer writer = new OutputStreamWriter(stream, "utf-8");
            result.writeTo(writer);
            writer.flush();
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
