package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.Template;
import java.io.*;
import java.util.*;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.EngineCycle;
import net.parostroj.timetable.output2.impl.EngineCyclesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Implements html output for engine cycles.
 *
 * @author jub
 */
public class GspEngineCyclesOutput extends GspOutput {

    public GspEngineCyclesOutput(Locale locale) {
        super(locale);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        // extract engine cycles
        EngineCyclesExtractor ece = new EngineCyclesExtractor(getCycles(params, diagram));
        List<EngineCycle> cycles = ece.getEngineCycles();

        // call template
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cycles", cycles);
        ResourceHelper.addTextsToMap(map, "ec_", this.getLocale(), "texts/html_texts");

        try {
            Template template = this.createTemplate(params, "/templates/groovy/engine_cycles.gsp");
            Writable result = template.make(map);
            Writer writer = new OutputStreamWriter(stream, "utf-8");
            result.writeTo(writer);
            writer.flush();
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    private List<TrainsCycle> getCycles(OutputParams params, TrainDiagram diagram) {
        OutputParam param = params.getParam("cycles");
        if (param != null && param.getValue() != null) {
            return (List<TrainsCycle>) param.getValue();
        }
        TrainsCycleSort s = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        return s.sort(diagram.getCycles(TrainsCycleType.ENGINE_CYCLE));
    }
}
