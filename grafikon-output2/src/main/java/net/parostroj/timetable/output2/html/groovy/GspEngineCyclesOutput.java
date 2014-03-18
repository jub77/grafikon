package net.parostroj.timetable.output2.html.groovy;

import groovy.text.Template;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.DefaultOutputParam;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.EngineCycle;
import net.parostroj.timetable.output2.impl.EngineCyclesExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

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
        try {
            // extract engine cycles
            EngineCyclesExtractor ece = new EngineCyclesExtractor(SelectionHelper.selectCycles(params, diagram, TrainsCycleType.ENGINE_CYCLE));
            List<EngineCycle> cycles = ece.getEngineCycles();

            // call template
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cycles", cycles);
            ResourceHelper.addTextsToMap(map, "ec_", this.getLocale(), "texts/html_texts");

            if (params.paramExistWithValue(DefaultOutputParam.TEXT_TEMPLATE)) {
                TextTemplate textTemplate = params.getParam(DefaultOutputParam.TEXT_TEMPLATE).getValue(TextTemplate.class);
                textTemplate.evaluate(stream, map);
            } else {
                Template template = this.getTemplate(params, "templates/groovy/engine_cycles.gsp", this.getClass().getClassLoader());
                this.writeOutput(stream, template, map);
            }
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }
}
